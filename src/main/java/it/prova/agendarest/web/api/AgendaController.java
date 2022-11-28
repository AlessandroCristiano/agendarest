package it.prova.agendarest.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.agendarest.dto.AgendaDTO;
import it.prova.agendarest.dto.UtenteDTO;
import it.prova.agendarest.model.Agenda;
import it.prova.agendarest.model.Ruolo;
import it.prova.agendarest.model.Utente;
import it.prova.agendarest.service.AgendaService;
import it.prova.agendarest.service.UtenteService;
import it.prova.agendarest.web.api.exception.AgendaNotFoundException;
import it.prova.agendarest.web.api.exception.IdNotNullForInsertException;
import it.prova.agendarest.web.api.exception.PermessoNegatoException;


@RestController
@RequestMapping("api/agenda")
public class AgendaController {
	
	@Autowired
	private AgendaService agendaService;
	
	@Autowired
	private UtenteService utenteService;
	
	@GetMapping("/allAdmin")
	public List<AgendaDTO> getAll() {
		// senza DTO qui hibernate dava il problema del N + 1 SELECT
		// (probabilmente dovuto alle librerie che serializzano in JSON)
		return AgendaDTO.createAgendaDTOListFromModelList(agendaService.listAllElementsEager(), true);
	}
	
	@GetMapping
	public List<AgendaDTO> getAgendaByUsername() {
		// senza DTO qui hibernate dava il problema del N + 1 SELECT
		// (probabilmente dovuto alle librerie che serializzano in JSON)
		return AgendaDTO.createAgendaDTOListFromModelList(agendaService.findByUsername(), true);
	}
	
	@GetMapping("/{id}")
	public AgendaDTO findById(@PathVariable(value = "id", required = true) long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		
		Agenda agenda = agendaService.caricaSingoloElementoConUtenza(id);
		
		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) != null || utenteLoggato.getId() == agenda.getUtente().getId()) {
			if (agenda == null)
				throw new AgendaNotFoundException("Agenda not found con id: " + id);

			return AgendaDTO.buildAgendaDTOFromModel(agenda, true);
		} else {
			throw new PermessoNegatoException("Non hai i permessi per visualizzare questo elemento!");
		}
		
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AgendaDTO createNew(@Valid @RequestBody AgendaDTO agendaInput) {
		// se mi viene inviato un id jpa lo interpreta come update ed a me (producer)
		// non sta bene
		if (agendaInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		agendaInput.setUtente(UtenteDTO.buildUtenteDTOFromModel(utenteService.findByUsername(username), true));

		Agenda agendaInserita = agendaService.inserisciNuovo(agendaInput.buildAgendaModel());
		
		return AgendaDTO.buildAgendaDTOFromModel(agendaInserita, true);
	}
	
	@PutMapping("/{id}")
	public AgendaDTO update(@Valid @RequestBody AgendaDTO agendaInput, @PathVariable(required = true) Long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		Agenda agenda = agendaService.caricaSingoloElemento(id);

		if (agenda == null)
			throw new AgendaNotFoundException("Agenda not found con id: " + id);
		
		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) != null || utenteLoggato.getId() == agenda.getUtente().getId()) {
			
			agendaInput.setId(id);
			agendaInput.setUtente(UtenteDTO.buildUtenteDTOFromModel(utenteService.findByUsername(username), true));
			Agenda agendaAggiornata = agendaService.aggiorna(agendaInput.buildAgendaModel());
			return AgendaDTO.buildAgendaDTOFromModel(agendaAggiornata, true);
			
		} else {
			throw new PermessoNegatoException("Non hai i permessi per modificare questo elemento!");
		}
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(required = true) Long id) {
		
		Agenda agenda = agendaService.caricaSingoloElemento(id);
		
		if (agenda == null)
			throw new AgendaNotFoundException("Agenda not found con id: " + id);
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		
		
		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) != null || utenteLoggato.getId() == agenda.getUtente().getId()) {
			
			agendaService.rimuovi(id);
			
		} else {
			throw new PermessoNegatoException("Non hai i permessi per Eliminare questo elemento!");
		}
	}

}
