package company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import persistence.PersistenceManager;

public class Area {

	//private Connection connection;
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
	 * Inserta un area nueva en la base de datos en la tabla area 
	 * También inserta las id del BussinesUnit al que pertenece el area nueva
	 * y el id de dicha area en la tabla b_unit_area
	 * @param conn conexión a la base de datos
	 * @param bUnit BusinessUnit al que pertenece el area a insertar
	 * @param area Area a insertar en la base de datos
	 */
	public void saveAreaToDB(Connection conn, BusinessUnit bUnit, Area area) {
		String sqlArea = "INSERT INTO area (area, descripcion) "
				+ "VALUES (?, ?);";
		String sqlBUnitArea = "INSERT INTO b_unit_area (b_unit_id, area_id) "
				+ "VALUES (?, ?);";
		try {
			PreparedStatement pstmA = conn.prepareStatement(sqlArea);
			PreparedStatement pstmBUA = conn.prepareStatement(sqlBUnitArea);
			pstmA.setString(1, area.getArea());
			pstmA.setString(2, area.getDescripcion());
			pstmA.executeUpdate();
			PersistenceManager.closePrepStatement(pstmA);
			pstmBUA.setInt(1, bUnit.getId());
			pstmBUA.setInt(2, area.getId());
			pstmBUA.executeUpdate();
			PersistenceManager.closePrepStatement(pstmBUA);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Actualiza los parámetros de un area que ya existe en la base de datos
	 * @param conn conexión a la base de datos
	 * @param area area que contiene los datos que actualizan al area ya existente
	 */
	public void updateAreaToDB(Connection conn, Area area) {
		String sql = "UPDATE area "
				+ "SET "
				+ "area = ?,"
				+ "descripcion = ? "
				+ "WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setString(1, area.getArea());
			pstm.setString(2, area.getDescripcion());
			pstm.setInt(3, area.getId());
			pstm.executeUpdate();
			PersistenceManager.closePrepStatement(pstm);
		} catch (SQLException e) {
			e.printStackTrace();
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
				area.setArea(results.getString(3));
				areaList.add(area);				
			}
			PersistenceManager.closePrepStatement(pstm);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return areaList;
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
