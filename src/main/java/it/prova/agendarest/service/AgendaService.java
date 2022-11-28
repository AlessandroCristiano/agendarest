package it.prova.agendarest.service;

import java.util.List;

import it.prova.agendarest.model.Agenda;


public interface AgendaService {
	
	List<Agenda> listAllElements();
	
	List<Agenda> listAllElementsEager();

	Agenda caricaSingoloElemento(Long id);
	
	Agenda caricaSingoloElementoConUtenza(Long id);

	Agenda aggiorna(Agenda agendaInstance);

	Agenda inserisciNuovo(Agenda agendaInstance);

	void rimuovi(Long idToRemove);

}
