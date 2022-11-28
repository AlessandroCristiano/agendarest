package it.prova.agendarest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.prova.agendarest.model.Agenda;

public class AgendaDTO {
	
	private Long id;
	
	@NotBlank(message = "{descrizione.notblank}")
	private String descrizione;
	
	@NotBlank(message = "{dataorainizio.notblank}")
	private LocalDateTime dataOraInizio;
	
	@NotBlank(message = "{dataorafine.notblank}")
	private LocalDateTime dataOraFine;
	
	@JsonIgnoreProperties(value = { "agende" })
	private UtenteDTO utente;

	public AgendaDTO() {
		super();
	}

	public AgendaDTO(Long id, String descrizione, LocalDateTime dataOraInizio, LocalDateTime dataOraFine) {
		super();
		this.id = id;
		this.descrizione = descrizione;
		this.dataOraInizio = dataOraInizio;
		this.dataOraFine = dataOraFine;
	}

	public AgendaDTO(Long id, String descrizione, LocalDateTime dataOraInizio, LocalDateTime dataOraFine, UtenteDTO utente) {
		super();
		this.id = id;
		this.descrizione = descrizione;
		this.dataOraInizio = dataOraInizio;
		this.dataOraFine = dataOraFine;
		this.utente = utente;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public LocalDateTime getDataOraInizio() {
		return dataOraInizio;
	}

	public void setDataOraInizio(LocalDateTime dataOraInizio) {
		this.dataOraInizio = dataOraInizio;
	}

	public LocalDateTime getDataOraFine() {
		return dataOraFine;
	}

	public void setDataOraFine(LocalDateTime dataOraFine) {
		this.dataOraFine = dataOraFine;
	}

	public UtenteDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteDTO utente) {
		this.utente = utente;
	}
	
	public Agenda buildAgendaModel() {
		Agenda result = new Agenda(this.id, this.descrizione, this.dataOraInizio, this.dataOraFine);
		if (this.utente != null)
			result.setUtente(this.utente.buildUtenteModelConAgende(true, true));

		return result;
	}
	
	public static AgendaDTO buildAgendaDTOFromModel(Agenda agendaModel, boolean includeUtenti) {
		AgendaDTO result = new AgendaDTO(agendaModel.getId(), agendaModel.getDescrizione(), agendaModel.getDataOraInizio(),
				agendaModel.getDataOraFine());

		if (includeUtenti)
			result.setUtente(UtenteDTO.buildUtenteDTOFromModel(agendaModel.getUtente()));

		return result;
	}
	
	public static List<AgendaDTO> createAgendaDTOListFromModelList(List<Agenda> modelListInput, boolean includeUtenti) {
		return modelListInput.stream().map(filmEntity -> {
			return AgendaDTO.buildAgendaDTOFromModel(filmEntity, includeUtenti);
		}).collect(Collectors.toList());
	}

	
	

}
