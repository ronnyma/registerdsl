package ai.transfinite.dsl;


import static ai.transfinite.dsl.Terms.FAMILIERELASJONSROLLE;
import static ai.transfinite.dsl.Terms.FOEDSELSAAR;
import static ai.transfinite.dsl.Terms.GREKER;
import static ai.transfinite.dsl.Terms.IDENTIFIKASJONSNUMMER;
import static ai.transfinite.dsl.Terms.IDENTIFIKATORTYPE;
import static ai.transfinite.dsl.Terms.OPPHOLDPAASVALBARD_PERIODE;
import static ai.transfinite.dsl.Terms.PERSONSTATUS;

import ai.transfinite.dsl.QueryDsl.Dsl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class RegisterQueryTest {

  private static void printQuery(ObjectMapper objectMapper, List<RegisterQuery> s1) {
    try {
      String s = objectMapper.writeValueAsString(s1);
      log.info("JSON: {}", s);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shoould_produceQuery_when_given_parameters() {
    Dsl dsl = new Dsl();
    List<RegisterQuery> query = dsl
        .har().noekkel(IDENTIFIKASJONSNUMMER).lik().verdi("01010112345").gjeldende()
        .har().noekkel(PERSONSTATUS).ulik().verdi("BOSATT").gjeldende()
        .harIkke().noekkel(FAMILIERELASJONSROLLE).lik().verdi("EKTEFELLE")
        .harIkke().noekkel(GREKER).ulik().verdi("greker")
        .har().noekkel(FOEDSELSAAR).fra().verdi("12").til().verdi("24")
        .har().noekkel(IDENTIFIKATORTYPE).lik().verdi("foedselsEllerDNummer").gjeldende() //TODO: mer enum/konstanter
        .har().noekkel(OPPHOLDPAASVALBARD_PERIODE).fra().verdi("2003").til().verdi("2009")
        .generer()
        .getRegisterQuery();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());

    printQuery(objectMapper, query);
  }

  @Test
  void shoould_produceQuery_when_given_parameters2() {
    Dsl dsl = new Dsl();
    List<RegisterQuery> query = dsl
        .har().noekkel(IDENTIFIKASJONSNUMMER)
        .lik().verdi("01010112345").gjeldende()
        .generer()
        .getRegisterQuery();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());

    printQuery(objectMapper, query);
  }
}