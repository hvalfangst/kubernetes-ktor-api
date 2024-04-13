import db.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import plugin.json.jsonMapper
import route.accounts
import route.customers
import route.loans
import service.AccountService
import service.CustomerService
import service.LoanService

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val podName = System.getenv("POD_NAME")
    DatabaseFactory.connectAndMigrate()

    install(ContentNegotiation) {
        jsonMapper()
    }

    val customerService = CustomerService()
    val accountService = AccountService()
    val loanService = LoanService()

    install(Routing) {
        customers(podName, customerService, accountService, loanService)
        accounts(podName, accountService)
        loans(podName, loanService)
    }
}