package frsf.isi.died.guia08.problema01.modelo;

import java.time.LocalDateTime;

import frsf.isi.died.guia08.problema01.exception.TareaIncorrectaException;

public class Tarea {

	private Integer id;
	private String descripcion;
	private Integer duracionEstimada;
	private Empleado empleadoAsignado;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private Boolean facturada;
	
	public void asignarEmpleado(Empleado e) throws TareaIncorrectaException {
		// si la tarea ya tiene un empleado asignado
		// y tiene fecha de finalizado debe lanzar una excepcion
		if(this.empleadoAsignado!=null) throw new TareaIncorrectaException("Esta tarea ya tiene empleado asignado");
		if(this.fechaFin!=null) throw new TareaIncorrectaException("Esta tarea ya ha sido finalizada");
		this.empleadoAsignado=e;
	}

	public Tarea(Integer id, String descripcion, Integer duracionEstimada, Empleado empleadoAsignado) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.duracionEstimada = duracionEstimada;
		this.empleadoAsignado = empleadoAsignado;
	}


	public Tarea() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getDuracionEstimada() {
		return duracionEstimada;
	}

	public void setDuracionEstimada(Integer duracionEstimada) {
		this.duracionEstimada = duracionEstimada;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Boolean getFacturada() {
		return facturada;
	}

	public void setFacturada(Boolean facturada) {
		this.facturada = facturada;
	}

	public Empleado getEmpleadoAsignado() {
		return empleadoAsignado;
	}
	public void setEmpleadoAsignado(Empleado empleadoAsignado) {
		this.empleadoAsignado = empleadoAsignado;
	}
	
	public String asCSV() {
		return this.id+"\";"+this.descripcion+"\";"+this.duracionEstimada+"\";"
				+this.fechaInicio+"\";"+this.fechaFin+"\";"+this.facturada+"\";"
				+this.empleadoAsignado.getCuil()+"\";"+this.empleadoAsignado.getNombre()+"\";";
	}
	
	
	
}
