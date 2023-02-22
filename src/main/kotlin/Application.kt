import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import db.DatabaseFactory
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import plugin.auth.basicAuthentication
import plugin.json.jsonMapper
import service.*
import plugin.route.*
import redis.clients.jedis.Jedis

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

    install(Authentication) {
        basicAuthentication()
    }

    install(Routing) {
        customers(podName, customerService, accountService, loanService)
        accounts(podName, accountService)
        loans(podName, loanService)
    }
}