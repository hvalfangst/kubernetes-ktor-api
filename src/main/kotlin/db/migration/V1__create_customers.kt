package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import model.*

class V1__create_customers: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            createCustomersTable()
            insertCustomer("Luke Skywalker", "Tatooine", "luke@skywalker.com", "19 BBY")
            insertCustomer("Leia Organa", "Alderaan", "leia@organa.com", "19 BBY")
            insertCustomer("Han Solo", "Corellia", "han@solo.com", "29 BBY")
            insertCustomer("Chewbacca", "Kashyyyk", "chewie@wookiee.com", "200 BBY")
            insertCustomer("Obi-Wan Kenobi", "Stewjon", "obi-wan@kenobi.com", "57 BBY")
            insertCustomer("Yoda", "Unknown", "yoda@jediorder.com", "896 BBY")
            insertCustomer("Lando Calrissian", "Socorro", "lando@calrissian.com", "31 BBY")
            insertCustomer("Mace Windu", "Haruun Kal", "mace@windu.com", "72 BBY")
            insertCustomer("Qui-Gon Jinn", "Unknown", "qui-gon@jinn.com", "92 BBY")
            insertCustomer("Padmé Amidala", "Naboo", "padme@amidala.com", "46 BBY")
            insertCustomer("Jar Jar Binks", "Naboo", "jar-jar@binks.com", "52 BBY")
            insertCustomer("C-3PO", "Unknown", "c-3po@droid.com", "112 BBY")
            insertCustomer("R2-D2", "Naboo", "r2-d2@droid.com", "33 BBY")
            insertCustomer("Darth Vader", "Executor-class Star Dreadnought", "darth.vader@sith.empire", "41.9 BBY")
            insertCustomer("Emperor Palpatine", "Imperial Palace", "palpatine@sith.empire", "84 BBY")
            insertCustomer("Kylo Ren", "Supremacy-class Star Destroyer", "kylo.ren@firstorder.com", "5 ABY")
            insertCustomer("Count Dooku", "Geonosis", "dooku@sith.empire", "102 BBY")
            insertCustomer("Asajj Ventress", "Rattatak", "ventress@sith.empire", "42 BBY")
            insertCustomer("Grand Moff Tarkin", "Death Star I", "tarkin@empire.com", "64 BBY")
            insertCustomer("Director Krennic", "Imperial Security Complex", "krennic@empire.com", "51 BBY")
            insertCustomer("Boba Fett", "Slave I", "boba.fett@bountyhunter.com", "31 BBY")
            insertCustomer("Jabba the Hutt", "Tatooine", "jabba@huttcartel.com", "600 BBY")
            insertCustomer("Greedo", "Mos Espa", "greedo@bountyhunter.com", "44 BBY")
            insertCustomer("IG-88", "Assassin Droid Factory", "ig-88@droid.net", "15 BBY")
            insertCustomer("Cad Bane", "Malastare", "cad.bane@bountyhunter.com", "67 BBY")
            insertCustomer("General Grievous", "Invisible Hand", "grievous@sith.empire", "Unknown")
            insertCustomer("Jango Fett", "Kamino", "jango.fett@bountyhunter.com", "66 BBY")
            insertCustomer("Ahsoka Tano", "Shili", "ahsoka.tano@jediorder.com", "36 BBY")
            insertCustomer("Anakin Skywalker", "Tatooine", "anakin.skywalker@jediorder.com", "41.9 BBY")
            insertCustomer("Jyn Erso", "Vallt", "jyn.erso@rebelalliance.com", "22 BBY")
            insertCustomer("Cassian Andor", "Fest", "cassian.andor@rebelalliance.com", "26 BBY")
            insertCustomer("Baze Malbus", "Jedha", "baze.malbus@rebelalliance.com", "55 BBY")
            insertCustomer("Chirrut Îmwe", "Jedha", "chirrut.imwe@guardianofwhills.com", "52 BBY")
            insertCustomer("K-2SO", "Unknown", "k2so@droid.com", "Unknown")
            insertCustomer("Director Orson Krennic", "Imperial Security Complex", "orson.krennic@empire.com", "52 BBY")
            insertCustomer("Enfys Nest", "Unknown", "enfys.nest@cloudridergang.com", "Unknown")
            insertCustomer("Dryden Vos", "Corellia", "dryden.vos@crimelord.com", "52 BBY")
        }
    }
    private fun createCustomersTable() {
        SchemaUtils.create(Customers)
    }

    private fun insertCustomer(name: String, address: String, email: String, dateOfBirth: String) {
        Customers.insert {
            it[this.name] = name
            it[this.address] = address
            it[this.email] = email
            it[this.dateOfBirth] = dateOfBirth
        }
    }
}