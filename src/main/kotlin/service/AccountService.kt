
package service

import org.jetbrains.exposed.sql.*
import db.DatabaseFactory.dbExec
import model.Account
import model.Accounts
import model.param.UpsertAccountRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import plugin.json.fromJson
import plugin.json.fromStringToList
import plugin.json.toJson
import redis.clients.jedis.Jedis


class AccountService {
    private val cache = Jedis("localhost", 6379)
    private val accountKey = "account"
    private val allAccountsKey = "all_accounts"
    private val allAccountsForCustomerKey = "all_accounts_for_customer"

    suspend fun getAllAccounts(): List<Account>  {
        var listOfAccounts = cache.get(allAccountsKey)

        if (listOfAccounts == null) {
            println("\nValue associated with key [$allAccountsKey] is not in cache!\n")
            listOfAccounts = toJson(dbExec {
                Accounts.selectAll()
                    .map { toAccount(it) }
                    .toList()
            })
            cache.set(allAccountsKey, listOfAccounts)
        } else {
            print("\nValue associated with key [$allAccountsKey] is present in cache!\n")
        }

        return fromStringToList(listOfAccounts)
    }

    suspend fun getAccount(id: Int): Account? {
        val key = "$accountKey:$id"
        var account = cache.get(key)

        if (account == null) {
            println("\nValue associated with key [$key] is not in cache!\n")
            account = toJson(dbExec {
                Accounts.select {
                    (Accounts.id eq id)
                }.map { toAccount(it) }
                    .singleOrNull()
            })
            cache.set(key, account)
        } else {
            print("\nValue associated with key [$key] is present in cache!\n")
        }

        return fromJson(account)
    }

    suspend fun getAllAccountsForCustomer(customerId: Int): List<Account> {
        val key = "$allAccountsForCustomerKey:$customerId"
        var listOfAccounts = cache.get(key)

        if(listOfAccounts == null) {
            println("\nValue associated with key [$key] is not in cache!\n")
            listOfAccounts = toJson(
                dbExec {
                    Accounts.select {
                        (Accounts.customerId eq customerId)
                    }.map { toAccount(it) }
                        .toList()
                }
            )
        } else {
            print("\nValue associated with key [$key] is present in cache!\n")
        }
     return fromStringToList(listOfAccounts);
    }

    suspend fun createAccount(request: UpsertAccountRequest): Account {
        val allAccounts = "$allAccountsForCustomerKey:${request.customerId}"
        var key = 0
        dbExec {
            key = (Accounts.insert {
                it[customerId] = request.customerId
                it[balance] = request.balance
                it[type] = request.type
            } get Accounts.id)
        }
        invalidateCache(allAccountsKey)

        if(cache.exists(allAccounts)) {
            invalidateCache(allAccounts)
        }

        return getAccount(key)!!
    }

    suspend fun updateAccount(id: Int, request: UpsertAccountRequest): Account? {
        val allAccountsForCustomer = "$allAccountsForCustomerKey:${request.customerId}"
            dbExec {
                Accounts.update({ Accounts.id eq id }) {
                    it[customerId] = request.customerId
                    it[balance] = request.balance
                    it[type] = request.type
                }
            }
        invalidateCache("$accountKey:$id")

        if(cache.exists(allAccountsForCustomer)) {
            invalidateCache(allAccountsForCustomer)
        }

        return getAccount(id)
    }

    suspend fun deleteAccount(id: Int): Boolean = dbExec {
        val customerId = Accounts.select { Accounts.id eq id }.singleOrNull()?.get(Accounts.customerId)

        if (customerId != null) {
            invalidateCache("$allAccountsForCustomerKey:$customerId")
        }

        val deletedAccounts = Accounts.deleteWhere {
            Accounts.id eq id
        }

        invalidateCache("$accountKey:$id")
        invalidateCache(allAccountsKey)

        // Return true if any accounts were deleted
        deletedAccounts > 0
    }

    suspend fun deleteAccounts(customerId: Int): Boolean = dbExec {
        val accountId = Accounts.select { Accounts.customerId eq customerId }.singleOrNull()?.get(Accounts.id)

        // Delete all accounts associated with the specified customer
        val deletedAccounts = Accounts.deleteWhere {
            Accounts.customerId eq customerId
        }

        if(accountId != null) {
            invalidateCache("$accountKey:$accountId")
        }

        invalidateCache(accountKey)
        invalidateCache("$allAccountsForCustomerKey:$customerId")

        // Return true if any accounts were deleted
        deletedAccounts > 0
    }

    private fun toAccount(row: ResultRow): Account =
        Account(
            id = row[Accounts.id],
            customerId = row[Accounts.customerId],
            balance = row[Accounts.balance],
            type = row[Accounts.type],
            delete = row[Accounts.delete]
        )

    private fun invalidateCache(key: String) {
        cache.del(key)
        println("Value associated with key [$key] has been deleted from the cache\n")
    }
}
