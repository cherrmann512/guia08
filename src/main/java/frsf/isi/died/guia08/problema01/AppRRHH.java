package frsf.isi.died.guia08.problema01;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import frsf.isi.died.guia08.problema01.exception.EmpleadoException;
import frsf.isi.died.guia08.problema01.exception.TareaIncorrectaException;
import frsf.isi.died.guia08.problema01.modelo.Empleado;
import frsf.isi.died.guia08.problema01.modelo.Empleado.Tipo;
import frsf.isi.died.guia08.problema01.modelo.Tarea;

public class AppRRHH {

	private List<Empleado> empleados;
	
	public void agregarEmpleadoContratado(Integer cuil,String nombre,Double costoHora) {
		// crear un empleado
		// agregarlo a la lista
		Empleado e = new Empleado(cuil, nombre, costoHora);
		e.setTipo(Tipo.CONTRATADO);
		this.empleados.add(e);
	}
	
	public void agregarEmpleadoEfectivo(Integer cuil,String nombre,Double costoHora) {
		// crear un empleado
		// agregarlo a la lista		
		Empleado e = new Empleado(cuil, nombre, costoHora);
		e.setTipo(Tipo.EFECTIVO);
		this.empleados.add(e);
	}
	
	public void asignarTarea(Integer cuil,Integer idTarea,String descripcion,Integer duracionEstimada) throws EmpleadoException {
		// crear un empleado
		// con el método buscarEmpleado() de esta clase
		// agregarlo a la lista
		Empleado emp = null;
		Optional<Empleado> empleado = buscarEmpleado(e-> e.getCuil()==cuil);
		if(empleado.isPresent()) {
			emp=empleado.get();
			
			try {
				Tarea t = new Tarea(idTarea, descripcion, duracionEstimada, emp);
				emp.asignarTarea(t);
			} catch (TareaIncorrectaException e1) {
				e1.getMessage();
			}
		}
		else throw new EmpleadoException("Empleado No Encontrado");
	}
	
	public void empezarTarea(Integer cuil,Integer idTarea) throws EmpleadoException {
		// busca el empleado por cuil en la lista de empleados
		// con el método buscarEmpleado() actual de esta clase
		// e invoca al método comenzar tarea
		Empleado emp = null;
		Optional<Empleado> empleado = buscarEmpleado(e-> e.getCuil()==cuil);
		if(empleado.isPresent()) {
			emp = empleado.get();
			emp.comenzar(idTarea);
		}
		else throw new EmpleadoException("Empleado No Encontrado");
	}
	
	public void terminarTarea(Integer cuil,Integer idTarea) throws EmpleadoException {
		// crear un empleado
		// agregarlo a la lista		
		Empleado emp = null;
		Optional<Empleado> empleado = buscarEmpleado(e-> e.getCuil()==cuil);
		if(empleado.isPresent()) {
			emp = empleado.get();
			emp.finalizar(idTarea);
		}
		else throw new EmpleadoException("Empleado No Encontrado");
	}

	public void cargarEmpleadosContratadosCSV(String nombreArchivo) {
		// leer datos del archivo
		// por cada fila invocar a agregarEmpleadoContratado
	}

	public void cargarEmpleadosEfectivosCSV(String nombreArchivo) {
		// leer datos del archivo
		// por cada fila invocar a agregarEmpleadoContratado		
	}

	public void cargarTareasCSV(String nombreArchivo) {
		// leer datos del archivo
		// cada fila del archivo tendrá:
		// cuil del empleado asignado, numero de la taera, descripcion y duración estimada en horas.
	}
	
	private void guardarTareasTerminadasCSV() {
		// guarda una lista con los datos de la tarea que fueron terminadas
		// y todavía no fueron facturadas
		// y el nombre y cuil del empleado que la finalizó en formato CSV 
	}
	
	private Optional<Empleado> buscarEmpleado(Predicate<Empleado> p){
		return this.empleados.stream().filter(p).findFirst();
	}

	public Double facturar() {
		this.guardarTareasTerminadasCSV();
		return this.empleados.stream()				
				.mapToDouble(e -> e.salario())
				.sum();
	}
}
