package se.itmo.ru.sharedthings

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Test: AbstractIntegrationTest() {

  @Test
  fun test(){
    jdbcTemplate.query("select * from user;"){
      Assertions.assertEquals(0, it.fetchSize)
    }
  }
}