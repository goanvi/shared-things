package se.itmo.ru.bookings

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(
    initializers = [
        AbstractIntegrationTest.PostgresDatasourceInitializer::class,
    ]
)

@AutoConfigureWebTestClient
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var r2dbcClient: DatabaseClient

    companion object {
        @JvmStatic
        val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16.0"))
            .apply { start() }
    }

    class PostgresDatasourceInitializer :
        ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.r2dbc.url=r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}",
                "spring.r2dbc.username=${postgres.username}",
                "spring.r2dbc.password=${postgres.password}",
                "spring.flyway.url=${postgres.jdbcUrl}",
                "spring.flyway.user=${postgres.username}",
                "spring.flyway.password=${postgres.password}"
            ).applyTo(applicationContext)
        }
    }
}