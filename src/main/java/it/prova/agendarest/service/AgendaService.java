package it.prova.agendarest.service;

import java.util.List;

import it.prova.agendarest.model.Agenda;
import it.prova.agendarest.model.Utente;


public interface AgendaService {
	
	List<Agenda> listAllElements();
	
	List<Agenda> listAllElementsEager();
	
	List<Agenda>findByUsername();

	Agenda caricaSingoloElemento(Long id);
	
	Agenda caricaSingoloElementoConUtenza(Long id);

	Agenda aggiorna(Agenda agendaInstance);

	Agenda inserisciNuovo(Agenda agendaInstance);
	
	Agenda inserisciNuovoConUtente(Agenda agendaInstance, Utente utenteInSessione);

	void rimuovi(Long idToRemove);
	
	Agenda findByDescrizione(String descrizione);

}
