package main.java.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.persistence.PersistenceManager;

public class Area {

	public static final String TABLE_NAME = "area";
//	public static final String B_UNIT_AREA_TABLE_NAME = "b_unit_area";
	private int id;
	private String area;
	private String descripcion;
	
	
	public Area(int id, String area, String descripcion) {
		//this.connection = connection;
		this.id = id;
		this.area = area;
		this.descripcion = descripcion;
	}
	
	public Area() {
		
	}
	
	/**
	 * Inserta un area nueva en la base de datos 
	 * @param conn conexión a la base de datos
	 * @param area area a insertar en la base de datos
	 * @return true si la insercion se hizo con éxito, false si no
	 */
	public boolean saveAreaToDB(Connection conn, Area area) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO area (area, descripcion) "
				+ "VALUES (?, ?);";
		try {
			pstm = conn.prepareStatement(sql);		
			pstm.setString(1, area.getArea());
			pstm.setString(2, area.getDescripcion());
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Inserta un nuevo registro en la tabla b_unit_area con los id del area nueva
	 * y de la unidad de negocio a la que pertenece
	 * @param conn conexión a la base de datos
	 * @param bUnit unidad de negocio a la que pertenece el area nueva
	 * @param area area nueva
	 * @return true si la insercion se hizo con éxito, false si no
	 */
	public boolean saveBUnitAreaToDB (Connection conn, BusinessUnit bUnit, Area area) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO b_unit_area (b_unit_id, area_id) "
				+ "VALUES (?, ?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bUnit.getId());
			pstm.setInt(2, area.getId());
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Si la inserción de un area nueva en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto Area.
	 * @param conn conexión a la base de datos
	 * @param area area nueva
	 * @return el area nueva insertada
	 */
	public Area addNewArea (Connection conn, Area area) {
		if (saveAreaToDB(conn, area)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			area.setId(id);
			return area;
//			if (saveBUnitAreaToDB (conn, bUnit, area)) {
//			}
		}	
		return null;
	}
	
	/**
	 * Si la inserción de un area nueva en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto Area. Tras ello, inserta un nuevo registro en la
	 * tabla b_unit_area con los id del area y de la unidad de negocio a la que pertenece
	 * @param conn conexión a la base de datos
	 * @param bUnit unidad de negocio a la que pertenece el area nueva
	 * @param area area nueva
	 * @return el area nueva insertada
	 */
	public Area addNewAreaToBunitArea (Connection conn, BusinessUnit bUnit, Area area) {
		if (saveAreaToDB(conn, area)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			area.setId(id);
			if (saveBUnitAreaToDB (conn, bUnit, area)) {
				return area;
			}
		}	
		return null;
	}
	
	/**
	 * Actualiza los valores de un area que ya existe en la base de datos
	 * @param conn conexión a la base de datos
	 * @param area area que contiene los datos que actualizan al area ya existente
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateAreaToDB(Connection conn, Area area) {
		PreparedStatement pstm = null;
		String sql = "UPDATE area "
				+ "SET "
				+ "area = ?,"
				+ "descripcion = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, area.getArea());
			pstm.setString(2, area.getDescripcion());
			pstm.setInt(3, area.getId());
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Obtiene la lista de areas del objeto BusinessUnit pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param bu objeto del que queremos recuperar sus areas
	 * @return lista de areas del objeto almacenados en la base de datos
	 */
	public List<Area> getAreasFromDB (Connection conn, BusinessUnit bu) {
		List<Area> areaList = new ArrayList<Area>();
		Area area = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT a.id, a.area, a.descripcion "
				+ "FROM area a, b_unit_area bua "
				+ "WHERE bua.b_unit_id = ? "
				+ "AND bua.area_id = a.id;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bu.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				area = new Area();
				area.setId(results.getInt(1));
				area.setArea(results.getString(2));
				area.setDescripcion(results.getString(3));
				areaList.add(area);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return areaList;
	}
	
	/**
	 * Devuelve el area al que pertenece el id entrado por parámetro
	 * @param bUnit BusinessUnit del que comprobamos la lista de areas
	 * @param id id del area buscado
	 * @return area con el id entrado por parámetro (null si no existe)
	 */
	public Area getAreaByName (BusinessUnit bUnit, String name) {
		List<Area> areaList = bUnit.getAreas();
		for (Area area: areaList) {
			if (area.getArea() == name) {
				return area;
			}
		}	
		return null;
	}
	
	/**
	 * Devuelve el area al que pertenece el id entrado por parámetro
	 * @param bUnit BusinessUnit del que comprobamos la lista de areas
	 * @param id id del area buscado
	 * @return area con el id entrado por parámetro (null si no existe)
	 */
	public Area getAreaById (BusinessUnit bUnit, int id) {
		List<Area> areaList = bUnit.getAreas();
		for (Area area: areaList) {
			if (area.getId() == id) {
				return area;
			}
		}	
		return null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
