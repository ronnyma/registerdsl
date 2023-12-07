package ai.transfinite.dsl;


import static ai.transfinite.dsl.Terms.FAMILIERELASJONSROLLE;
import static ai.transfinite.dsl.Terms.FOEDSELSAAR;
import static ai.transfinite.dsl.Terms.GREKER;
import static ai.transfinite.dsl.Terms.IDENTIFIKASJONSNUMMER;
import static ai.transfinite.dsl.Terms.IDENTIFIKATORTYPE;
import static ai.transfinite.dsl.Terms.OPPHOLDPAASVALBARD_PERIODE;
import static ai.transfinite.dsl.Terms.PERSONSTATUS;
import static ai.transfinite.dsl.Terms.UKJENTBOSTED;

import ai.transfinite.dsl.QueryDsl.Dsl;
import ai.transfinite.elastic.QueryGenerator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.jackson.JacksonJsonpGenerator;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class RegisterQueryTest {


  public static void queryToJson(SearchRequest request) {
    final StringWriter writer = new StringWriter();
    try (final JacksonJsonpGenerator generator = new JacksonJsonpGenerator(new JsonFactory().createGenerator(writer))) {
      request.serialize(generator, new JacksonJsonpMapper());
    } catch (IOException e) {
      log.info("Feil: {}", e.getMessage());
    }
    log.info("Query: {}", writer);
  }
  private static void printQuery(List<RegisterQuery> s1) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
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
        .harIkke().noekkel(GREKER).ulik()
        .harIkke().noekkel(UKJENTBOSTED).ulik().verdi("0301").gjeldende()
        .har().noekkel(FOEDSELSAAR).interval().fra().verdi("12").til().verdi("24")
        .har().noekkel(IDENTIFIKATORTYPE).lik().verdi("foedselsEllerDNummer").gjeldende() //TODO: mer enum/konstanter
        .har().noekkel(OPPHOLDPAASVALBARD_PERIODE).interval().fra().verdi("2003").til().verdi("2009")
        .generer()
        .getRegisterQuery();

    SearchRequest build = QueryGenerator.generate(query).build();


    queryToJson(build);
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

    printQuery(query);
  }
}