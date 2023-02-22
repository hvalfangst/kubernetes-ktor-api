package service

import org.jetbrains.exposed.sql.*
import db.DatabaseFactory.dbExec
import model.Accounts
import model.Customer
import model.Customers
import model.Loans
import model.param.CreateCustomerRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import plugin.json.fromJson
import plugin.json.fromStringToList
import plugin.json.toJson
import redis.clients.jedis.Jedis

class CustomerService {
    private val jedis = Jedis("localhost", 6379)

    suspend fun getAllCustomers(): List<Customer> {
        val key = "all_customers"
        var listOfCustomers = jedis.get(key)

        if (listOfCustomers == null) {
            println("\nValue associated with key [$key] is not in cache!\n")
            listOfCustomers = toJson(dbExec {
                Customers.selectAll()
                    .map { toCustomer(it) }
                    .toList()
            })
            jedis.set(key, listOfCustomers)
        } else {
            print("\nValue associated with key [$key] is present in cache!\n")
        }

        return fromStringToList(listOfCustomers)
    }

    suspend fun getCustomer(id: Int): Customer? {
        val key = "customer:$id"
        var customer = jedis.get(key)

        if (customer == null) {
            println("\nValue associated with key [$key] is not in cache!\n")
            customer = toJson(dbExec {
                Customers.select {
                    (Customers.id eq id)
                }.map { toCustomer(it) }
                    .singleOrNull()
            })
            jedis.set(key, customer)
        } else {
            print("\nValue associated with key [$key] is present in cache!\n")
        }

        return fromJson(customer)
    }

    suspend fun createCustomer(customer: CreateCustomerRequest): Customer {
        var key = 0
        dbExec {
            key = (Customers.insert {
                it[name] = customer.name
                it[address] = customer.address
                it[email] = customer.email
                it[dateOfBirth] = customer.dateOfBirth
            } get Customers.id)
        }
        invalidateCache("all_customers")
        return getCustomer(key)!!
    }

    suspend fun deleteCustomer(customerId: Int): Boolean = dbExec {

        // Delete all accounts associated with the specified customer
        val deletedAccounts = Accounts.deleteWhere {
            Accounts.customerId eq customerId
        }

        // Delete all loans associated with the specified customer
        val deletedLoans = Loans.deleteWhere {
            Loans.customerId eq customerId
        }

        // Delete the customer record now that all references are deleted
        val deletedCustomers = Customers.deleteWhere {
            id eq customerId
        }

        invalidateCache("customer:$customerId")
        invalidateCache("all_customers")

        // Return true if any accounts or customers were deleted
        deletedAccounts > 0 || deletedLoans > 0 || deletedCustomers > 0
    }

    suspend fun updateCustomer(id: Int, customer: CreateCustomerRequest): Customer? {
            dbExec {
                Customers.update({ Customers.id eq id }) {
                    it[name] = customer.name
                    it[address] = customer.address
                    it[email] = customer.email
                    it[dateOfBirth] = customer.dateOfBirth
                }
            }
            invalidateCache("all_customers")
            invalidateCache("customer:$id")
            return getCustomer(id)
        }


    private fun toCustomer(row: ResultRow): Customer =
        Customer(
            id = row[Customers.id],
            name = row[Customers.name],
            address = row[Customers.address],
            email = row[Customers.email],
            dateOfBirth = row[Customers.dateOfBirth]
        )

    private fun invalidateCache(key: String) {
        jedis.del(key)
        println("\nValue associated with key [$key] has been deleted from the cache\n")
    }
}