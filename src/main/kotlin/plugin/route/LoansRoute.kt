package plugin.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Loan
import model.param.CreateLoanRequest
import model.param.Response
import service.LoanService

fun Route.loans(podName: String, loanService: LoanService) {
    route("/loans") {

            get {
                val loans: List<Loan> = loanService.getAllLoans()
                call.respond(Response(podName, loans))
            }

            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val loan: Loan? = loanService.getLoan(id)

                when (loan != null) {
                    true ->  call.respond(Response(podName, loan))
                    false -> {
                        val errorMessage = "The requested loan does not exist on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
             }
          }

            post {
                val request = call.receive<CreateLoanRequest>()
                val loan: Loan?  = loanService.createLoan(request)
                call.respond(Response(podName, loan))
            }

            put("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<CreateLoanRequest>()
                val updatedLoan: Loan?  = loanService.updateLoan(id, request)

                when (updatedLoan != null) {
                    true -> call.respond(Response(podName, updatedLoan))
                    false ->  {
                        val errorMessage = "The requested loan does not exist on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()

                when (loanService.deleteLoan(id)) {
                    true -> call.respond("Loan with id $id has been deleted on pod $podName")
                    false -> {
                        val errorMessage = "The requested loan does not exist on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }

            delete("/all/{customerId}") {
                val customerId = call.parameters["customerId"]!!.toInt()

                when (loanService.deleteLoans(customerId)) {
                    true -> call.respond("All loans associated with $customerId has been deleted on pod $podName")
                    false -> {
                        val errorMessage = "No loans were identified for the given customer on pod $podName"
                        call.respond(HttpStatusCode.NotFound, errorMessage)
                    }
                }
            }
    }