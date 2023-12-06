package ai.transfinite.dsl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Terms implements DslPart{
  IDENTIFIKASJONSNUMMER("identifikator.foedselsellerdnummer"),
  PERSONSTATUS("status.status"),
  FOEDSELSAAR("foedsel.foedselsaar"),
  OPPHOLDPAASVALBARD_PERIODE("svalbard.aar"), //TODO: denne finnes ikke, men brukes under utvikling
  IDENTIFIKATORTYPE("identifikator.identifikatortype"),
  GREKER("greker.greker"),
  FAMILIERELASJONSROLLE("familierelasjon.minrolleforperson");

  private String term;

  private Terms(String term) {
    this.term = term;
  }

  public static Terms fromValue(String v) {
    try {
      return valueOf(v);
    } catch (IllegalArgumentException e) {
      log.error("Fant ikke operator {}", e.getMessage());
      return null;
    }
  }

  public String value() {
    return term;
  }
}
