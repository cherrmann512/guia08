package frsf.isi.died.guia08.problema01.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import frsf.isi.died.guia08.problema01.exception.TareaIncorrectaException;

public class Empleado {

	public enum Tipo { CONTRATADO,EFECTIVO}; 
	
	private Integer cuil;
	private String nombre;
	private Tipo tipo;
	private Double costoHora;
	private List<Tarea> tareasAsignadas;
	
	private Function<Tarea, Double> calculoPagoPorTarea;		
	private Predicate<Tarea> puedeAsignarTarea;
	
	

	public Empleado(Integer cuil, String nombre, Double costoHora) {
		super();
		this.cuil = cuil;
		this.nombre = nombre;
		this.costoHora = costoHora;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	public Integer getCuil() {
		return cuil;
	}

	public void setCalculoPagoPorTarea(Function<Tarea, Double> calculoPagoPorTarea) {
		this.calculoPagoPorTarea = calculoPagoPorTarea;
	}

	public void setPuedeAsignarTarea(Predicate<Tarea> puedeAsignarTarea) {
		this.puedeAsignarTarea = puedeAsignarTarea;
	}

	public Double salario() {
		// cargar todas las tareas no facturadas
		// calcular el costo
		// marcarlas como facturadas.
		double salario=0.0;
		List<Tarea> nofacturadas = this.tareasAsignadas.stream()
							.filter(t->t.getFacturada()==false)
							.collect(Collectors.toList());
		for (Tarea tar : nofacturadas) {
			salario+= calculoPagoPorTarea.apply(tar);
			tar.setFacturada(true);
		}
		return salario;
	}
	
	/**
	 * Si la tarea ya fue terminada nos indica cuaal es el monto según el algoritmo de calculoPagoPorTarea
	 * Si la tarea no fue terminada simplemente calcula el costo en base a lo estimado.
	 * @param t
	 * @return
	 */
	public Double costoTarea(Tarea t) {
		if(t.getFechaFin()==null) return this.costoHora*t.getDuracionEstimada();
		long diasASumar = t.getDuracionEstimada()/4;
		LocalDateTime fechaFinEstimada = t.getFechaInicio().plusDays(diasASumar);
		switch(tipo) {
		case CONTRATADO:
			if (fechaFinEstimada.getDayOfYear()-t.getFechaFin().getDayOfYear()>2) {
				setCalculoPagoPorTarea(tar-> tar.getDuracionEstimada()*0.75*this.costoHora);
			}
			if(fechaFinEstimada.getDayOfYear()-t.getFechaFin().getDayOfYear()<0) {
				setCalculoPagoPorTarea(tar-> tar.getDuracionEstimada()*1.3*this.costoHora);
			}
			break;
		case EFECTIVO:
			if(fechaFinEstimada.getDayOfYear()-t.getFechaFin().getDayOfYear()<0) {
				setCalculoPagoPorTarea(tar-> tar.getDuracionEstimada()*1.2*this.costoHora);
			}
		}
		return calculoPagoPorTarea.apply(t);
	}
		

	public Boolean asignarTarea(Tarea t) throws TareaIncorrectaException {
		if(t.getFechaFin()!=null) throw new TareaIncorrectaException("la tarea ya está finalizada");
		if(t.getEmpleadoAsignado()!=null) throw new TareaIncorrectaException("la tarea ya tiene empleado a cargo");
		
		switch (tipo) {
		case EFECTIVO:
			setPuedeAsignarTarea(tar-> this.horasPendientes()<15);
			break;
		case CONTRATADO:
			setPuedeAsignarTarea(tar-> this.tareasPendientes()<5);
		}
		if(puedeAsignarTarea.test(t)) {
			this.tareasAsignadas.add(t);
			t.asignarEmpleado(this);
			return true;
		}
		else return false;
	}
	
	public int horasPendientes() {
		return this.tareasAsignadas.stream()
									.filter(t-> t.getFechaFin()==null)
									.mapToInt(t->t.getDuracionEstimada())
									.sum();
	}
	
	public long tareasPendientes() {
		return this.tareasAsignadas.stream()
									.filter(t-> t.getFechaFin()==null)
									.count();
	}
	
	public void comenzar(Integer idTarea) {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de inicio la fecha y hora actual
		String fechaActual = LocalDateTime.now().toString();
		try {
			this.comenzar(idTarea, fechaActual);
		} catch (TareaIncorrectaException e) {
			e.getMessage();
		}
		
	}
	
	public void finalizar(Integer idTarea) {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de finalizacion la fecha y hora actual
		String fechaActual = LocalDateTime.now().toString();
		try {
			this.finalizar(idTarea, fechaActual);
		} catch (TareaIncorrectaException e) {
			e.getMessage();
		}
	}

	public void comenzar(Integer idTarea,String fecha) throws TareaIncorrectaException {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de finalizacion la fecha y hora actual
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("“dd-MM-yyyy HH:mm”");
		LocalDateTime fechaComienzo = LocalDateTime.parse(fecha, formatter);
		Optional<Tarea> tareaOpt = this.tareasAsignadas.stream()
														.filter(t->t.getId() == idTarea)
														.findFirst();
		if(tareaOpt.isEmpty()) {
			throw new TareaIncorrectaException("Tarea no encontrada");
		}
		else {
			tareaOpt.get().setFechaInicio(fechaComienzo);
		}
		
	}
	
	public void finalizar(Integer idTarea,String fecha) throws TareaIncorrectaException {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de finalizacion la fecha y hora actual
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("“dd-MM-yyyy HH:mm”");
		LocalDateTime fechaFinalizacion = LocalDateTime.parse(fecha, formatter);
		
		Optional<Tarea> tareaOpt = this.tareasAsignadas.stream()
														.filter(t->t.getId() == idTarea)
														.findFirst();
		if(tareaOpt.isEmpty()) {
			throw new TareaIncorrectaException("Tarea no encontrada");
		}
		else {
			tareaOpt.get().setFechaFin(fechaFinalizacion);
		}
	}
}
