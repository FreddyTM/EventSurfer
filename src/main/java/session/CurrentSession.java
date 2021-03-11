package main.java.session;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import main.java.company.Area;
import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.persistence.PersistenceManager;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;
import main.java.types_states.UserType;

public class CurrentSession {

	//Instancia única de la sesión en curso para toda la aplicación
	private static CurrentSession session;
	//Usuario que inicia sesión en la aplicación
	private User user;
	//Unidad de negocio al que pertenece el usuario que inicia sesión en la aplicación
	private BusinessUnit bUnit;
	//Empresa a la que pertenece la unidad de negocio
	private Company company;
	//Conexión con la base de datos
	private Connection connection;
	//Temporizador de comprobación de cambios en la base de datos
	private Timer timer;
	//Acción que devuelve el programa a la pantalla de login
	private Action logOutAction;
	
	//ATRIBUTOS VOLATILE
	//Se declaran volatile para que todos los temporizadores de actualización de datos
	//del programa accedan a una copia única de las variables
	
	//Registra la fecha y hora de la última actualización que consta en la base de datos
	//en el momento de iniciar la sesión. Se actualiza con la fecha y hora en que se produzcan
	//cambios en la base de datos que afecten a company, a bUnit, o a cualquiera de los objetos
	//que contiene bUnit.
	private volatile Timestamp dateTimeReference;
	//Lista de tablas actualizadas por el temporizador de comprobación de cambios
	private volatile Map <String, Timestamp> updatedTables = new LinkedHashMap<String, Timestamp>();
	//Variable de control de actualización de usuarios si ésta se produce por la actualización
	//de unidades de negocio
	private volatile boolean usersUpdated = false;
	
	//Registran la lista de monitores del sistema, y el monitor en el que se ejecuta la aplicación
	private GraphicsDevice [] displays;
	private GraphicsDevice currentDisplay;
	//Registra si una ventana de aviso ya ha sido lanzada
	public boolean alertShown = false;
	
	private CurrentSession() {
		
	}
	
	//Solo existe una instancia de CurrentSession para toda la aplicación
	public static CurrentSession getInstance() {
		if (session == null) {
			session = new CurrentSession();
		}
		return session;
	}
		
	/**
	 * Carga todos los datos de la base de datos. La compañía,
	 * la unidad de negocio y el usuario se almacenan en los
	 * atributos de la clase
	 * @param conn conexión con la base de datos
	 * @param bUnitId unidad de negocio a la que pertenece el usuario
	 * que identificamos con el parámetro userId
	 * @param userId id del usuario. Será un usuario administrador,
	 * ya que es el único tipo de usuario que tiene acceso a los datos
	 * de todas las unidades de negocio
	 */
	public void loadAllData (Connection conn, int bUnitId, int userId) {
		//Carga de tipos de usuarios, tipos de eventos y estados de eventos
		loadTypesStates(conn);
		//Cargamos datos de la compañía
		company = new Company().getCompanyFromDB(conn);
		//Cargamos las unidades de negocio de la compañía
		List<BusinessUnit> bUnitList = new BusinessUnit().getBusinessUnitsFromDB(conn, company);
		company.setBusinessUnits(bUnitList);
		for (BusinessUnit unit: bUnitList) {
			if (unit.getId() == bUnitId) {
				//Asignamos la unidad de negocio a bUnit
				bUnit = unit;
			}
		}
		//Para localizar y asignar la BusinessUnit de la sesión, también debería funcionar
		// this.bunit = new BusinessUnit().getBusinessUnitById (company, bUnitId);
		
		//Para cada unidad de negocio, cargamos sus usuarios, areas y eventos
		for (BusinessUnit unit: company.getBusinessUnits()) {
			List<User> userList = new User().getUsersFromDB(conn, unit);
			unit.setUsers(userList);
			List<Area> areaList = new Area().getAreasFromDB(conn, unit);
			unit.setAreas(areaList);
			List<Event> eventList = new Event().getEventsFromDB(conn, unit);
			unit.setEvents(eventList);
			for (Event event: unit.getEvents()) {
				List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
				event.setUpdates(eUpdate);
			}
		}
		//Asignamos el usuario que abre sesión a user
		user = new User().getUserById(bUnit, userId);
		//Registramos fecha y hora de la carga de datos de la sesión
		dateTimeReference = PersistenceManager.getLatestTimestampFromDb(connection);
		//Iniciamos la comprobación periódica de actualizaciones
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 10000, 60000);
	}
	
	/**
	 * Carga los datos de la unidad de negocio a la que pertenece el usuario
	 * que abre la sesión. La compañía, la unidad de negocio y el usuario
	 * se almacenan en los atributos de la clase
	 * @param conn conexión con la base de datos
	 * @param bUnitId id de la unidad de negocio
	 * @param userId id del usuario que abre la sesión
	 */
	public void loadCurrentSessionData(Connection conn, int bUnitId, int userId) {
		//Carga de tipos de usuarios, tipos de eventos y estados de eventos
		loadTypesStates(conn);
		//Cargamos datos de la compañía
		company = new Company().getCompanyFromDB(conn);
		//Cargamos la unidad de negocio que tiene el id bUnitId
		List<BusinessUnit> bUnitList = new BusinessUnit().getBusinessUnitsFromDB(conn, company);
		for (BusinessUnit unit: bUnitList) {
			if (unit.getId() == bUnitId) {
				//Añadimos a la compañía la unidad de negocio a la que pertenece el usuario
				company.getBusinessUnits().add(unit);
				//Asignamos la unidad de negocio a bUnit
				bUnit = unit;
			}
		}
		//Cargamos sus usuarios, areas y eventos de la unidad de negocio
		List<User> userList = new User().getUsersFromDB(conn, bUnit);
		bUnit.setUsers(userList);
		//Asignamos el usuario que abre sesión a user
		user = new User().getUserById(bUnit, userId);
		List<Area> areaList = new Area().getAreasFromDB(conn, bUnit);
		bUnit.setAreas(areaList);
		List<Event> eventList = new Event().getEventsFromDB(conn, bUnit);
		bUnit.setEvents(eventList);
		for (Event event: bUnit.getEvents()) {
			List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
			event.setUpdates(eUpdate);
		}
		//Registramos fecha y hora de la carga de datos de la sesión
		dateTimeReference = PersistenceManager.getLatestTimestampFromDb(connection);
		//Iniciamos la comprobación periódica de actualizaciones
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 10000, 60000);
	}
	
//	/**
//	 * Método de carga de datos unificado. ** Declarado, pero sin desarrollar todavía **
//	 * @param conn conexión con la base de datos
//	 * @param bUnitId id de la unidad de negocio
//	 * @param userId id del usuario que abre la sesión
//	 */
//	public void loadData (Connection conn, int bUnitId, int userId) {
//		//Crear método en user getUserTypeFromDb(Connection conn, int userId) para filtrar la carga de datos en
//		//función del tipo de usuario que abrió sesión
//	}
	
	/**
	 * Carga los tipos de usuarios, tipos de eventos y estados de eventos almacenados en la base
	 * de datos, y los agrupa en un objeto contenedor TypesStatesContainer
	 * @param conn conexión con la base de datos
	 */
	public void loadTypesStates(Connection conn) {
		//Lista de tipos de usuario
		UserType userTypeList = new UserType();
		userTypeList.loadData(conn);	
		//Lista de tipos de eventos
		EventType eventTypeList = new EventType();
		eventTypeList.loadData(conn);	
		//Lista de estados de eventos
		EventState eventStateList = new EventState();
		eventStateList.loadData(conn);		
		//Mandamos las listas a un objeto contenedor
		TypesStatesContainer.setuType(userTypeList);
		TypesStatesContainer.setEvType(eventTypeList);
		TypesStatesContainer.setEvState(eventStateList);
	}
	
	/**
	 * Da formato a la fecha y la hora del Timestamp pasado por parámetro
	 * @param timestamp Timestamp a formatear
	 * @param pattern patrón de formateo, si es null se usa el patrón por defecto del método
	 * @return Timestamp formateado
	 */
	public String formatTimestamp(Timestamp timestamp, String pattern) {
		if (pattern == null) {
			pattern = "EEEE, dd-MM-yyyy HH:mm:ss";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String niceTimestamp = formatter.format(timestamp);
		return niceTimestamp;
	}
	
	/**
	 * Devuelve el programa a la pantalla de login si el usuario que abrió sesión ha sido desactivado por otro usuario desde otra
	 * sesión. Se mostrará un mensaje informativo antes de cerrar la sesión local. El usuario que abrió sesión ya no podrá hacer
	 * login de nuevo hasta que no sea reactivado por algún administrador.
	 * @param tableName origen de la desactivación del usuario, bien sea por la desactivación de la unidad de negocio a la que pertenece
	 * o por la desactivación directa del usuario
	 */
	public void backToLogin(String tableName, GraphicsDevice [] displays, GraphicsDevice currentDisplay) {
		
		String infoMessage = "";
		String title = "";
		if (tableName.equals(BusinessUnit.TABLE_NAME)) {
			infoMessage = "SE HA DESACTIVADO LA UNIDAD DE NEGOCIO A LA QUE PERTENECE EL USUARIO QUE ABRIÓ SESIÓN.\n"
					+ "TODOS LOS USUARIOS DE DICHA UNIDAD DE NEGOCIO HAN SIDO DESACTIVADOS TAMBIÉN.\n"
					+ "CONTACTE CON UN ADMINISTRADOR SI NECESITA RECUPERAR SU ACCESO AL PROGRAMA.";
			title = "Unidad de negocio y usuarios desactivados";
		} else if (tableName.equals(User.TABLE_NAME)) {
			infoMessage = "SE HA DESACTIVADO AL USUARIO QUE HA ABIERTO SESIÓN.\n"
					+ "CONTACTE CON UN ADMINISTRADOR SI NECESITA RECUPERAR SU ACCESO AL PROGRAMA.";
			title = "Usuario desactivado";
		}

		Frame messageFrame = new Frame(currentDisplay.getDefaultConfiguration());
		Rectangle frameRectangle = messageFrame.getBounds();
	    int paneWidth = frameRectangle.width;
	    int paneHeight = frameRectangle.height;
		
	    int coordinateX = currentDisplay.getDefaultConfiguration().getBounds().x;
	    int coordinateY = currentDisplay.getDefaultConfiguration().getBounds().y;
	    int currentWidth = 0;
	    int currentHeight = 0;
		//Centrado de pantalla multimonitor
		for (int i = 0; i < displays.length; i++) {
		    if (currentDisplay.getIDstring().equals(displays[i].getIDstring())) {
				currentWidth = currentDisplay.getDisplayMode().getWidth();
				currentHeight = currentDisplay.getDisplayMode().getHeight();
				messageFrame.setBounds((currentWidth - paneWidth) / 2 + coordinateX, (currentHeight - paneHeight) / 2 + coordinateY, paneWidth, paneHeight);
		    }
		}
		JOptionPane.showMessageDialog(messageFrame, infoMessage, title, JOptionPane.WARNING_MESSAGE);
		
		//Replicamos la acción de logout del selector
		new JButton(logOutAction).doClick();
	}

	/**
	 * Clase que realiza la comprobación de las tablas que se han actualizado
	 * con posterioridad a la última carga de datos de la sesión en curso
	*/
	private class TimerJob extends TimerTask {
		
//		private volatile boolean usersUpdated = false;
		
		@Override
		public void run() {
			if (user != null) {
				//Debug
				System.out.println("Usuario: " + user.getUserAlias());
			} else {
				System.out.println("Usuario: sin definir");
			}
			System.out.println("Comprobando actualización de datos de la sesión");
			
			CurrentSession.this.updatedTables.clear();
			Connection conn = session.getConnection();
			if (conn == null) {
				conn = PersistenceManager.getConnection();
			}
			//Registramos el timestamp de la sessión y lo asignamos también a una
			//variable temporal que registrará los incrementos que puedan existir 
			//en la base de datos
			Timestamp sessionDateTime = session.getDateTimeReference();
			Timestamp tempDateTime = session.getDateTimeReference();;
			String tableName = "";
			Statement stm = null;
			ResultSet results = null;
			String sql = "SELECT * "
					+ "FROM last_modification";
			try {
				//Debug
				System.out.println("Dentro del try");
				
				stm = conn.createStatement();
				results = PersistenceManager.getResultSet(stm, sql);
				//Para cada tabla, comprobamos su timestamp
				while (results.next()) {
					
					//Debug
					System.out.println("Dentro del while");
					
					tableName = results.getString(1);
					Timestamp dateTimeDb = results.getTimestamp(2);
					//Si el timestamp de la tabla es posterior al de la sesión, se ha
					//producido una actualización que no tenemos registrada.
					if (sessionDateTime.before(dateTimeDb) ) {
						
						//Debug
						System.out.println("Dentro del if");
						
						//Actualizar objetos correspondientes a table_name
						switch(tableName) {
							case "user_type":
								UserType userTypeList = new UserType();
								userTypeList.loadData(conn);
								TypesStatesContainer.setuType(userTypeList);
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							case "event_type":
								EventType eventTypeList = new EventType();
								eventTypeList.loadData(conn);
								TypesStatesContainer.setEvType(eventTypeList);
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							case "event_state":
								EventState eventStateList = new EventState();
								eventStateList.loadData(conn);
								TypesStatesContainer.setEvState(eventStateList);
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							case "company":
								
								//Debug
								System.out.println("Dentro del case company");
								
								//Actualizamos la compañía de la sesión
								company.refresh(conn);
								
								//Debug
								System.out.println(session.getCompany().getNombre());
								
								//Añadimos la tabla company a la lista de tablas actualizadas
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								
								//Debug
								System.out.println(tableName);
								System.out.println(dateTimeDb.toString());
								System.out.println("Tamaño del Map: " + CurrentSession.this.updatedTables.size());
								
								break;
							case "business_unit":
								
								//Debug
								System.out.println("Dentro del case business_unit");
								
								//Carga de tipos de usuarios, tipos de eventos y estados de eventos
								loadTypesStates(conn);
								
								//Recargamos la lista de unidades de negocio de la base de datos
								List<BusinessUnit> bUnits = new BusinessUnit().getBusinessUnitsFromDB(conn, company);
								
								//Localizamos la unidad de negocio del usuario que abrió sesión
								BusinessUnit updatedBunit = null;
								for (BusinessUnit oneUnit : bUnits) {
									if (user != null) {
										if (oneUnit.getId() == user.getbUnit().getId()) {
											updatedBunit = oneUnit;
										}
									}
								}
								
								//Comprobamos que la unidad de negocio del usuario que abrió sesión no ha sido deshabilitada
								if(updatedBunit != null && updatedBunit.isActivo() == false && usersUpdated == false) {
									//No hace falta que el case user actualize usuarios y nos devuelva también a la pantalla de login
									usersUpdated = true;
									//Si no hemos vuelto a la pantalla de login directamente por la acción del usuario
									if (!alertShown) {
										//Back to login
										backToLogin(BusinessUnit.TABLE_NAME, displays, currentDisplay);
									}
									alertShown = false;
								//Si la unidad de negocio del usuario que abrió sesión sigue activa, recargamos datos
								} else if (updatedBunit != null){
									//Filtramos la lista de unidades de negocio en función del tipo de usuario que abrió sesión
									//Si es un usuario administrador, se recargan todas las unidades de negocio
									if (user.getUserType().equals("ADMIN")) {
													
										//Debug
										System.out.println("Recargando todas las unidades de negocio. Usuario administrador");
										
										company.setBusinessUnits(bUnits);
										for (BusinessUnit oneUnit : company.getBusinessUnits()) {
											if (oneUnit.getId() == bUnit.getId()) {
												//Reasignamos la unidad de negocio de la sesión
												bUnit = oneUnit;
												break;
											}
										}									
									//Si es un usuario manager o user, solo recargamos su unidad de negocio, que es la misma que la
									//de la sesión
									} else {
										for (BusinessUnit oneUnit : bUnits) {
											
											//Debug
											System.out.println("Recargando la unidad de negocio del usuario manager o user");
											
											if (oneUnit.getId() == bUnit.getId()) {
												company.getBusinessUnits().clear();
												company.getBusinessUnits().add(oneUnit);
												//Reasignamos la unidad de negocio de la sesión
												bUnit = oneUnit;
												break;
											}
										}
									}
									//Recargamos los datos de la lista de unidades de negocio actualizadas							
									for (BusinessUnit oneUnit : company.getBusinessUnits()) {
										List<User> userList = new User().getUsersFromDB(conn, oneUnit);
										oneUnit.setUsers(userList);
										List<Area> areaList = new Area().getAreasFromDB(conn, oneUnit);
										oneUnit.setAreas(areaList);
										List<Event> eventList = new Event().getEventsFromDB(conn, oneUnit);
										oneUnit.setEvents(eventList);
										for (Event event: oneUnit.getEvents()) {
											List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
											event.setUpdates(eUpdate);
										}
									}

									//Asignamos el usuario que abre sesión a user	
									for (User oneUser : updatedBunit.getUsers()) {
										if (oneUser.getId() == user.getId()) {
											user = oneUser;
											break;
										}
									}
									//Registramos que la recarga de los usuarios actualizados ya se ha hecho
									usersUpdated = true;
									//Añadimos la tabla business_unit a la lista de tablas actualizadas
									CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
									//Añadimos la tabla user a la lista de tablas actualizadas para cubrir la posibilidad de que la
									//edición de una unidad de negocio haya afectado al estado de sus usuarios
									CurrentSession.this.updatedTables.put(User.TABLE_NAME, dateTimeDb);
								}
								break;
							case "user":
								
								//Debug
								if (user != null) {
									if (user.getUserAlias() == "" || user.getUserAlias() == null) {
										System.out.println("Usuario: ninguno");
									} else {
										System.out.println("Usuario: " + user.getUserAlias());
									} 
								}
								System.out.println("Dentro del case user");
								
								//Comprobamos que una actualización previa de las unidades de negocio no haya hecho ya la
								//correspondiente recarga de los usuarios actualizados, para no repetirla.
								if (!usersUpdated) {
									
									//Recargamos los datos de los usuarios de todas las unidades de negocio
									for (BusinessUnit oneUnit : company.getBusinessUnits()) {
										List<User> userList = new User().getUsersFromDB(conn, oneUnit);
										oneUnit.setUsers(userList);
									}
									//Localizamos al usuario que abrió sesión
									User updatedUser = null;
									for (User oneUser : user.getbUnit().getUsers()) {
										if (oneUser.getId() == user.getId()) {
											updatedUser = oneUser;
										}
									}
									//Comprobamos que el usuario que abrió sesión no ha sido deshabilitado
									if(updatedUser.isActivo() == false) {
										if (!alertShown) {
											//Back to login
										backToLogin(User.TABLE_NAME, displays, currentDisplay);
										}
										alertShown = false;
									//Si el usuario que abrió sesión sigue activo, lo reasignamos 
									} else {
										user = updatedUser;	
									}
								}
								//Reset usersUpdated
								usersUpdated = false;
								
//								//Añadimos la tabla user a la lista de tablas actualizadas
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							case "area":
								List<Area> areaList = new Area().getAreasFromDB(conn, session.getbUnit());
								//Asignamos la lista de areas actualizada a la unidad de negocio de la sesión
								session.getbUnit().setAreas(areaList);
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							case "event":
								List<Event> eventList = new Event().getEventsFromDB(conn, session.getbUnit());
								//Asignamos la lista de eventos actualizada a la unidad de negocio de la sesión
								session.getbUnit().setEvents(eventList);
								//Asignamos a cada evento de la unidad de negocio sus actualizaciones
								for (Event event: session.getbUnit().getEvents()) {
									List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
									event.setUpdates(eUpdate);
								}
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							case "event_update":
								//Asignamos la lista de actualizaciones de eventos actualizada a cada evento de 
								//la unidad de negocio de la sesión
								for (Event event: session.getbUnit().getEvents()) {
									List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
									event.setUpdates(eUpdate);
								}
								CurrentSession.this.updatedTables.put(tableName, dateTimeDb);
								break;
							default:
								//Error. Tabla desconocida
						}				
						//Actualizamos el timestamp temporal para que acabe registrando
						//el mayor valor que encuentre en todo el Resultset
						if(tempDateTime.before(dateTimeDb)) {
							tempDateTime = dateTimeDb;
						}
					}
				}
				//Si el timestamp de la sesión es anterior al timestamp temporal tras comprobar las actualizaciones
				if (sessionDateTime.before(tempDateTime)) {
					//Actualizamos timestamp de la sesión con el valor del timestamp temporal
					session.setDateTimeReference(tempDateTime);
				}
				
				//Actualizar el panel que esté visible si su información ha cambiado
				//1º - Identificar panel visible
				//2º - Comprobar si el panel visible tiene datos de la tabla que ha cambiado
				//3º - Recargar datos del panel visible
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BusinessUnit getbUnit() {
		return bUnit;
	}

	public void setbUnit(BusinessUnit bUnit) {
		this.bUnit = bUnit;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getDateTimeReference() {
		return dateTimeReference;
	}

	public void setDateTimeReference(Timestamp dateTimeReference) {
		this.dateTimeReference = dateTimeReference;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Timer getTimer() {
		return timer;
	}

	public Action getLogOutAction() {
		return logOutAction;
	}

	public void setLogOutAction(Action logOutAction) {
		this.logOutAction = logOutAction;
	}

	public Map <String, Timestamp> getEventStates() {
		return updatedTables;
	}

	public void setEventStates(Map <String, Timestamp> eventStates) {
		this.updatedTables = eventStates;
	}

	public Map<String, Timestamp> getUpdatedTables() {
		return updatedTables;
	}

	public void setUpdatedTables(Map<String, Timestamp> updatedTables) {
		this.updatedTables = updatedTables;
	}

	public boolean isUsersUpdated() {
		return usersUpdated;
	}

	public void setUsersUpdated(boolean usersUpdated) {
		this.usersUpdated = usersUpdated;
	}

	public GraphicsDevice[] getDisplays() {
		return displays;
	}

	public void setDisplays(GraphicsDevice[] displays) {
		this.displays = displays;
	}

	public GraphicsDevice getCurrentDisplay() {
		return currentDisplay;
	}

	public void setCurrentDisplay(GraphicsDevice currentDisplay) {
		this.currentDisplay = currentDisplay;
	}

	public boolean isAlertShown() {
		return alertShown;
	}

	public void setAlertShown(boolean alertShown) {
		this.alertShown = alertShown;
	}

}
