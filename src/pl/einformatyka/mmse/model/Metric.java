package pl.einformatyka.mmse.model;

public class Metric {
	
	private String name;
	private String boaCode;

	public Metric(String name,String boaCode){
		this.name = name;
		this.boaCode = boaCode;
	}
	public String getBoaCode() {
		return boaCode;
	}
	public void setBoaCode(String boaCode) {
		this.boaCode = boaCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}

}
