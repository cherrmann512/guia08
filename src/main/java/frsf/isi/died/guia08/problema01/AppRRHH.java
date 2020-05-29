package frsf.isi.died.guia08.problema01;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
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
	List<Tarea> tareasNoFacturadas;	
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

	public void cargarEmpleadosContratadosCSV(String nombreArchivo) throws FileNotFoundException, IOException {
		// leer datos del archivo
		// por cada fila invocar a agregarEmpleadoContratado
		try(Reader fileReader = new FileReader(nombreArchivo)){
			try(BufferedReader in = new BufferedReader(fileReader)){
				String linea = null;
				while((linea = in.readLine())!=null) {
					String[] fila = linea.split(";"); 
					agregarEmpleadoContratado(Integer.valueOf(fila[0]), fila[1], Double.valueOf(fila[2]));
				}
			}
		}
	}

	public void cargarEmpleadosEfectivosCSV(String nombreArchivo) throws FileNotFoundException, IOException {
		// leer datos del archivo
		// por cada fila invocar a agregarEmpleadoContratado
		try(Reader fileReader = new FileReader(nombreArchivo)){
			try(BufferedReader in = new BufferedReader(fileReader)){
				String linea = null;
				while((linea = in.readLine())!=null) {
					String[] fila = linea.split(";"); 
					agregarEmpleadoEfectivo(Integer.valueOf(fila[0]), fila[1], Double.valueOf(fila[2]));
				}
			}
		}
	}

	public void cargarTareasCSV(String nombreArchivo) throws FileNotFoundException, IOException {
		// leer datos del archivo
		// cada fila del archivo tendrá:
		// cuil del empleado asignado, numero de la taera, descripcion y duración estimada en horas.
		try(Reader fileReader = new FileReader(nombreArchivo)){
			try(BufferedReader in = new BufferedReader(fileReader)){
				String linea = null;
				while((linea = in.readLine())!=null) {
					String[] fila = linea.split(";"); 
					Empleado e = new Empleado();
					e.setCuil(Integer.valueOf(fila[0]));
					Tarea t = new Tarea();
					t.setId(Integer.valueOf(fila[1]));
					t.setDescripcion(fila[2]);
					t.setDuracionEstimada(Integer.valueOf(fila[3]));
					t.setEmpleadoAsignado(e);
					tareasNoFacturadas.add(t);
				}
			}
		}
		
	}
	
	private void guardarTareasTerminadasCSV() throws IOException {
		// guarda una lista con los datos de la tarea que fueron terminadas
		// y todavía no fueron facturadas
		// y el nombre y cuil del empleado que la finalizó en formato CSV 
		try(Writer fileWriter = new FileWriter("tareasNoFacturadas.csv", true)){
			try(BufferedWriter out = new BufferedWriter(fileWriter)){
				for (Empleado e : empleados) {
					for (Tarea t : e.getTareasAsignadas()) {
						if (t.getFechaFin()!=null && !t.getFacturada()) {
							out.write(t.asCSV()+System.getProperty("line.separator"));
						}
					}
				}
			}
			
		}
		
	}
	
	private Optional<Empleado> buscarEmpleado(Predicate<Empleado> p){
		return this.empleados.stream().filter(p).findFirst();
	}

	public Double facturar() {
		try {
			this.guardarTareasTerminadasCSV();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return this.empleados.stream()				
				.mapToDouble(e -> e.salario())
				.sum();
	}
	
}
