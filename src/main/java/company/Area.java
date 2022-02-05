package main.java.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.persistence.PersistenceManager;

/**
 * Almacena la información referente a las areas y proporciona los métodos necesarios para
 * realizar las operaciones CRUD con la base de datos y con el resto de componentes del 
 * programa
 * @author Alfred Tomey
 */
public class Area {

	public static final String TABLE_NAME = "area";
	public static final String B_UNIT_AREA_TABLE_NAME = "b_unit_area";
	private int id;
	private String areaNombre;
	private String descripcion;
	
	
	public Area(int id, String area, String descripcion) {
		this.id = id;
		this.areaNombre = area;
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
			pstm.setString(1, area.getAreaNombre());
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
	 * Borra un area de la base de datos 
	 * @param conn conexión a la base de datos
	 * @param area area a borrar de la base de datos
	 * @return true si el borrado se hizo con éxito, false si no
	 */
	public boolean deleteAreaFromDB(Connection conn, Area area) {
		PreparedStatement pstm = null;
		String sql = "DELETE FROM area "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, area.getId());
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
	 * Borra de la lista de areas del centro de trabajo pasado por parámetro
	 * el área pasada por parámetro, si es que está en la lista
	 * @param bUnit centro de trabajo de la que borramos el area
	 * @param area area a borrar
	 * @return true si se ha borrado alguna area, false si no
	 */
	public boolean deleteArea(BusinessUnit bUnit, Area area) {
		boolean areaDeleted = false;
		for (int i = 0; i < bUnit.getAreas().size(); i++) {
			if (bUnit.getAreas().get(i).getId() == area.getId()) {
				bUnit.getAreas().remove(i);
				areaDeleted = true;
				break;
			}
		}
		return areaDeleted;
	}
	
	/**
	 * Borra los registros de la tabla b_unit_area con el id del area pasada por
	 * parámetro. El area dejará de estar asignada a cualquier centro de trabajo.
	 * @param conn conexión a la base de datos
	 * @param area area que deja de estar asignada al  centro de trabajo
	 * @return true si el borrado se hizo con éxito, false si no
	 */
	public boolean deleteBUnitAreaFromDB (Connection conn, Area area) {
		PreparedStatement pstm = null;
		String sql = "DELETE FROM b_unit_area "
				+ "WHERE area_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, area.getId());
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
	 * Borra el registro de la tabla b_unit_area con los id del area y el centro de trabajo
	 * pasados por parámetro. El area dejará de estar asignada a dicho centro de trabajo.
	 * @param conn conexión a la base de datos
	 * @param bUnit centro de trabajo al que está asignado el area
	 * @param area area que deja de estar asignada al centro de trabajo
	 * @return true si el borrado se hizo con éxito, false si no
	 */
	public boolean deleteOneBUnitAreaFromDB (Connection conn, BusinessUnit bUnit, Area area) {
		PreparedStatement pstm = null;
		String sql = "DELETE FROM b_unit_area "
				+ "WHERE area_id = ? "
				+ "AND b_unit_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, area.getId());
			pstm.setInt(2, bUnit.getId());
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
	 * y del centro de trabajo a la que pertenece
	 * @param conn conexión a la base de datos
	 * @param bUnit centro de trabajo al que pertenece el area nueva
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
		}	
		return null;
	}
	
	/**
	 * Si la inserción de un area nueva en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto Area. Tras ello, inserta un nuevo registro en la
	 * tabla b_unit_area con los id del area y del centro de trabajo a la que pertenece
	 * @param conn conexión a la base de datos
	 * @param bUnit centro de trabajo al que pertenece el area nueva
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
			pstm.setString(1, area.getAreaNombre());
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
	 * Obtiene la lista de areas del centro de trabajo pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param bUnit centro de trabajo del que queremos recuperar sus areas
	 * @return lista de areas del centro de trabajo almacenados en la base de datos
	 */
	public List<Area> getAreasFromDB (Connection conn, BusinessUnit bUnit) {
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
			pstm.setInt(1, bUnit.getId());
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
	 * Obtiene la lista de todas las areas almacenadas en la base de datos
	 * @param conn conexión con la base de datos
	 * @return lista de areas almacenadas en la base de datos
	 */
	public List<Area> getAllAreasFromDB(Connection conn) {
		List<Area> areaList = new ArrayList<Area>();
		Area area = null;
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT * FROM area;";
		try {
			stm = conn.createStatement();
			results = stm.executeQuery(sql);
			while (results.next()) {
				area = new Area();
				area.setId(results.getInt(1));
				area.setArea(results.getString(2));
				area.setDescripcion(results.getString(3));
				areaList.add(area);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(stm);
		}
		return areaList;
	}
	
	/**
	 * Comprueba si el area pasada por parámetro está asignada a algun centro de trabajo
	 * @param conn conexión con la base de datos
	 * @param area area a comprobar
	 * @return true si el area está asignada, false si no lo está
	 */
	public boolean isAllocatedArea(Connection conn, Area area) {
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT DISTINCT area_id "
				+ "FROM b_unit_area "
				+ "ORDER BY area_id;";
		try {
			stm = conn.createStatement();
			results = stm.executeQuery(sql);
			while (results.next()) {
				if (results.getInt(1) == area.getId()) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(stm);
		}
		return false;
	}
	
	/**
	 * Devuelve el area al que pertenece el nombre de area entrado por parámetro
	 * @param bUnit centro de trabajo del que comprobamos la lista de areas
	 * @param name nombre del area buscado
	 * @return area con el nombre entrado por parámetro (null si no existe)
	 */
	public Area getAreaByName (BusinessUnit bUnit, String name) {
		List<Area> areaList = bUnit.getAreas();
		for (Area area: areaList) {
			if (area.getAreaNombre() == name) {
				return area;
			}
		}	
		return null;
	}
	
	/**
	 * Devuelve el area al que pertenece el nombre de area entrado por parámetro
	 * @param areaList Lista de areas de la que comprobamos el nombre
	 * @param name nombre del area buscado
	 * @return area con el nombre entrado por parámetro (null si no existe)
	 */
	public Area getAreaByName (List<Area> areaList, String name) {
		for (Area area: areaList) {
			if (area.getAreaNombre() == name) {
				return area;
			}
		}	
		return null;
	}
	
	/**
	 * Devuelve el area al que pertenece el id entrado por parámetro
	 * @param bUnit centro de trabajo del que comprobamos la lista de areas
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

	public String getAreaNombre() {
		return areaNombre;
	}

	public void setArea(String area) {
		this.areaNombre = area;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
