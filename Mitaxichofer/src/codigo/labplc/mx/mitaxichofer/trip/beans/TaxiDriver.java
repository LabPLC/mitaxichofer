package codigo.labplc.mx.mitaxichofer.trip.beans;

import java.io.Serializable;

/**
 * 
 * @author zace3d
 * 
 */
public class TaxiDriver implements Serializable {
	/**
	 * ID taxidriver serialization
	 */
	private static final long serialVersionUID = -1098245708702892529L;
	private String name;
	private String lastName;
	private String foto;
	private String placa;
	private String taxiModelCar;
	private String tipo;

	
	public TaxiDriver(){}
	
	public TaxiDriver(String name, String lastName,String foto, String placa,
			String taxiModelCar,String tipo) {
		super();
		this.name = name;
		this.lastName = lastName;
		this.placa = placa;
		this.taxiModelCar = taxiModelCar;
		this.tipo =  tipo;
	}

	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getTaxiModelCar() {
		return taxiModelCar;
	}

	public void setTaxiModelCar(String taxiModelCar) {
		this.taxiModelCar = taxiModelCar;
	}
	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
}