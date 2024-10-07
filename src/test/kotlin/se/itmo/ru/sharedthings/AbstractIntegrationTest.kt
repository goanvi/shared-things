package se.itmo.ru.sharedthings

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
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
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {

  @Autowired
  lateinit var mocMvc: MockMvc

  @Autowired
  lateinit var jdbcTemplate: JdbcTemplate

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