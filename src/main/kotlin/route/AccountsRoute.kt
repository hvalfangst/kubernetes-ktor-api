package route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Account
import model.param.UpsertAccountRequest
import model.param.Response
import service.AccountService

fun Route.accounts(podName: String, accountService: AccountService) {
    route("/accounts") {

            get {
                val accounts: List<Account> = accountService.getAllAccounts()
                call.respond(Response(podName, accounts))
            }

            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val account: Account? = accountService.getAccount(id)

                when (account != null) {
                    true ->  call.respond(Response(podName, account))
                    false -> {
                        val errorMessage = "The requested account does not exist on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }

            post {
                val request = call.receive<UpsertAccountRequest>()
                val account: Account? = accountService.createAccount(request)
                call.respond(Response(podName, account))
            }

            put("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<UpsertAccountRequest>()
                val updatedAccount: Account? = accountService.updateAccount(id, request)

                when (updatedAccount != null) {
                    true -> call.respond(Response(podName, updatedAccount))
                    false -> {
                        val errorMessage = "The requested account does not exist on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()

                when (accountService.deleteAccount(id)) {
                    true -> call.respond("Account with $id has been deleted on pod $podName")
                    false -> {
                        val errorMessage = "The requested account does not exist on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }

            delete("/all/{customerId}") {
                val customerId = call.parameters["customerId"]!!.toInt()

                when (accountService.deleteAccounts(customerId)) {
                    true -> call.respond("All accounts associated with $customerId has been deleted on pod $podName")
                    false -> {
                        val errorMessage = "No accounts were identified for the given customer on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }
        }
}