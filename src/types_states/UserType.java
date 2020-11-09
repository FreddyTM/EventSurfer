package types_states;

import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserType {

	private Connection connection;
	//Map <id, user_type> Tabla user_type de la base de datos
	private Map <Integer, String> userTypes = new LinkedHashMap<Integer, String>();
	
	public UserType (Connection connection) {
		this.connection = connection;
	}
	
	//Devuelve el número de tipos de usuario almacenados en la base de datos
	public int numeroTiposDeUsuario() {
		return userTypes.size();
	}
	
	//Devuelve un array con el nombre de los diferentes tipos de usuarios
	public String[] TiposDeUsuario() {
		String[] tiposDeUsuario = new String[numeroTiposDeUsuario()];
		Collection<String> tipos = userTypes.values();
		tiposDeUsuario = tipos.toArray(tiposDeUsuario);
		//Connection newConnection = UserType.connection;
		return tiposDeUsuario;
	}
	
	//Retorna la clave del tipo de usuario pasado por parámetro
	public int getClave (Map <Integer, String> usuarios, String tipoDeUsuario) {
		for (Integer clave : usuarios.keySet()) {
			if (tipoDeUsuario.equals(usuarios.get(clave))) {
				return clave;
			}
		}
		//El tipo de usuario no existe
		return -1;
		
		// Program to get Map's key from value in Java
//		class Main
//		{
//		    public static <K, V> K getKey(Map<K, V> map, V value) {
//		        for (K key : map.keySet()) {
//		            if (value.equals(map.get(key))) {
//		                return key;
//		            }
//		        }
//		        return null;
//		    }
//		 
//		    public static void main(String[] args) {
//		        Map<String, Integer> hashMap = new HashMap();
//		        hashMap.put("A", 1);
//		        hashMap.put("B", 2);
//		        hashMap.put("C", 3);
//		 
//		        System.out.println(getKey(hashMap, 2));        // prints B
//		    }
//		}
		
	}
	
	//Retorna el usuario que corresponde a la clave pasada por parámetro
	public String getTipoDeUsuario (Map <Integer, String> usuarios, int clave) {
		if (usuarios.containsKey(clave)) {
			return usuarios.get(clave);
		} else {
			return null;
		}
	}
	
	//Conecta con la base de datos y almacena cada tipo de usuario con su clave
	// en userTypes
	public void cargaDatos() {
		
	}
}
