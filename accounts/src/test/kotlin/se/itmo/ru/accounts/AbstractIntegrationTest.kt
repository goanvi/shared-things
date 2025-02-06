package se.itmo.ru.accounts

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
)
@ContextConfiguration(
    initializers = [
        AbstractIntegrationTest.PostgresDatasourceInitializer::class,
    ]
)

@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

    companion object {
        @JvmStatic
        val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16.0"))
            .apply { start() }
    }

    class PostgresDatasourceInitializer :
        ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=${postgres.jdbcUrl}",
                "spring.datasource.username=${postgres.username}",
                "spring.datasource.password=${postgres.password}"
            ).applyTo(applicationContext)
        }
    }
}