package com.veracode.verademo.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.veracode.verademo.utils.UserSession;


/**
 * @author johnadmin
 *
 */
@Controller
@Scope("request")
public class UserController {
	private static final Logger logger = LogManager.getLogger("VeraDemo:UserController");

	@Autowired
	private UserSession theUser;
	
	private String dbConnStr = "jdbc:mysql://localhost/blab?user=blab&password=z2^E6J4$;u;d";

	
	
	/**
	 * @param target
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLogin(@RequestParam(value="target", required=false) String target, Model model) {
		logger.info("Entering showLogin");
		if (null != target) 
			model.addAttribute("target", target);
		else
			model.addAttribute("target", "");
		return "login";
	}

	/**
	 * @param username
	 * @param password
	 * @param target
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String processLogin(@RequestParam(value="user", required=true) String username, 
                               @RequestParam(value="password", required=true) String password, 
                               @RequestParam(value="target", required=false) String target, Model model) {
		String nextView = "login";
		
		logger.info("Entering processLogin");
		
		Connection connect = null;
        /* START BAD CODE */
		Statement sqlStatement = null;
        /* END BAD CODE */
        /* START GOOD CODE 
		PreparedStatement sqlStatement = null;
        /* END GOOD CODE */

		try {
			// Get the Database Connection
			logger.info("Creating the Database connection");
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(dbConnStr);

			/* START BAD CODE */
			// Execute the query
			logger.info("Creating the Statement");
			String sqlQuery = "select * from users where username='" + username + "' and password='" + password + "';";
			sqlStatement = connect.createStatement();
			logger.info("Execute the Statement");
			ResultSet result = sqlStatement.executeQuery(sqlQuery);
			// Did we find exactly 1 user that matched?
			if (result.first()) {
				// OK we have found the user, lets setup their Session object
				logger.info("User Found. Setting up UserSession object");
				theUser.setUserID(result.getInt(1));
				theUser.setUsername(result.getString(2));
				theUser.setRealName(result.getString(6));
				theUser.setBlabName(result.getString(7));
				theUser.setLoggedIn(true);

				logger.info("Login complete. Redirecting (target=" + (null==target ? "null" : target) + ")");
				if (0 != target.length())
					nextView = "redirect:" + target;
				else 
					nextView = "redirect:feed";
			} else {
				logger.info("User Not Found");
				// Login failed...
				model.addAttribute("error", "Login failed. Please try again.");
				model.addAttribute("target", target);
			}
			/* END BAD CODE */

			/* START GOOD GODE 
			String sqlQuery = "insert into users (username, password, date_created) values (?, ?, ?)";
			logger.info("Preparing the PreparedStatement");
			sqlStatement = connect.prepareStatement(sqlQuery);
			logger.info("Setting parameters for the PreparedStatement");
			sqlStatement.setString(1, username);
			sqlStatement.setString(2, password);
			sqlStatement.setDate(3,  new java.sql.Date(Calendar.getInstance().getTime().getTime()));
			logger.info("Executing the PreparedStatement");
			Boolean result = sqlStatement.execute();
			logger.info("PreparedStatement Execution returned " + (result ? "true which is unexpected" : "false which is expected"));
			/* END GOOD CODE */
		}catch (SQLException exceptSql) {
			logger.error(exceptSql);
			model.addAttribute("error", exceptSql.getMessage());
			model.addAttribute("target", target);
        } catch (ClassNotFoundException cnfe) {
			logger.error(cnfe);
			model.addAttribute("error", cnfe.getMessage());
			model.addAttribute("target", target);
        	
        } finally {
        	try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
        	} catch (SQLException exceptSql) {
    			logger.error(exceptSql);
    			model.addAttribute("error", exceptSql.getMessage());
    			model.addAttribute("target", target);
            }
        	try {
                if (connect != null){
                    connect.close();
                }
            } catch (SQLException exceptSql) {
    			logger.error(exceptSql);
    			model.addAttribute("error", exceptSql.getMessage());
    			model.addAttribute("target", target);
            }
        }
		return nextView;
	}
	
	@RequestMapping(value="/logout", method={ RequestMethod.GET, RequestMethod.POST })
	public String processLogout(@RequestParam(value="type", required=false) String type, Model model) {
		logger.info("Entering processLogout");
		logger.info("Clearing UserSession");
		theUser.setBlabName("");
		theUser.setUserID(0);
		theUser.setUsername("");
		theUser.setLoggedIn(false);
		logger.info("Redirecting to Login...");
		return "redirect:login";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegister(@RequestParam(value="type", required=false) String type, Model model) {
		logger.info("Entering showRegister");
		
		return "register";
	}

	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegister(@RequestParam(value="user", required=true) String username, 
			                      @RequestParam(value="password", required=true) String password, 
			                      @RequestParam(value="cpassword", required=true) String cpassword, 
			                      @RequestParam(value="realName", required=true) String realName,
			                      @RequestParam(value="blabName", required=true) String blabName,Model model) {
		logger.info("Entering processRegister");

		String nextView = "register";
		// Do the password and cpassword parameters match ?
		if (0 != password.compareTo(cpassword)) {
			logger.info("Password and Confirm Password do not match");
			model.addAttribute("error", "The Password and Confirm Password values do not match. Please try again.");
			return "register";
		}
		Connection connect = null;
        /* START BAD CODE 
		Statement sqlStatement = null;
        /* END BAD CODE */
        /* START GOOD CODE */
		PreparedStatement sqlStatement = null;
        /* END GOOD CODE */

		try {
			// Get the Database Connection
			logger.info("Creating the Database connection");
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(dbConnStr);

			/* START BAD CODE 
			// Execute the query
			String sqlQuery = "insert into users (username, password) values ('" + username + "', '" + password + "')";
			sqlStatement = connect.createStatement();
			Boolean result = sqlStatement.execute(sqlQuery);
			/* END BAD CODE */

			/* START GOOD GODE */
			String sqlQuery = "insert into users (username, password, date_created, real_name, blab_name) values (?, ?, ?, ?, ?)";
			logger.info("Preparing the PreparedStatement");
			sqlStatement = connect.prepareStatement(sqlQuery);
			logger.info("Setting parameters for the PreparedStatement");
			sqlStatement.setString(1, username);
			sqlStatement.setString(2, password);
			sqlStatement.setDate(3,  new java.sql.Date(Calendar.getInstance().getTime().getTime()));
			sqlStatement.setString(4, realName);
			sqlStatement.setString(5, blabName);
			
			logger.info("Executing the PreparedStatement");
			Boolean result = sqlStatement.execute();
			// If there is a record...
			if (result) {
				//failre
				model.addAttribute("error", "Failed to modify your preferences. Please try again");
			} else {
				nextView = "login";
			}
			nextView = "redirect:blabbers";
			logger.info("PreparedStatement Execution returned " + (result ? "true which is unexpected" : "false which is expected"));
			/* END GOOD CODE */
		}catch (SQLException exceptSql) {
			logger.error(exceptSql);
        } catch (ClassNotFoundException cnfe) {
			logger.error(cnfe);
        	
        } finally {
        	try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
        	} catch (SQLException exceptSql) {
    			logger.error(exceptSql);
            }
        	try {
                if (connect != null){
                    connect.close();
                }
            } catch (SQLException exceptSql) {
    			logger.error(exceptSql);
            }
        }
		return nextView;
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String showProfile(@RequestParam(value="type", required=false) String type, Model model) {
		logger.info("Entering showProfile");
		model.addAttribute("realName", theUser.getRealName());
		model.addAttribute("blabName", theUser.getBlabName());
		return "profile";
	}

	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String processProfile(@RequestParam(value="realName", required=true) String realName, 
			                     @RequestParam(value="blabName", required=true) String blabName, Model model) {
		String nextView = "redirect:feed";
		logger.info("Entering processProfile");
		if (!theUser.getLoggedIn()) {
			logger.info("User is not Logged In - redirecting...");
			nextView = "redirect:login?target=feed";
		} else {
			logger.info("User is Logged In - continuing...");
			
	 		Connection connect = null;
			PreparedStatement update = null;
			String updateSql = "UPDATE users SET real_name=?, blab_name=? WHERE userid=?;";

			try {
				logger.info("Getting Database connection");
				// Get the Database Connection
				Class.forName("com.mysql.jdbc.Driver");
				connect = DriverManager.getConnection(dbConnStr);
				
				// 
				logger.info("Preparing the update Prepared Statement");
				update = connect.prepareStatement(updateSql);
				update.setString(1, realName);
				update.setString(2, blabName);
				update.setInt(3, theUser.getUserID());
							
				logger.info("Executing the update Prepared Statement");
				boolean updateResult = update.execute();
				
				// If there is a record...
				if (updateResult) {
					//failre
					model.addAttribute("error", "Failed to modify your preferences. Please try again");
				} else {
					theUser.setRealName(realName);
					theUser.setBlabName(blabName);
				}
				nextView = "redirect:blabbers";
				
			}catch (SQLException exceptSql) {
				logger.error(exceptSql);
	        } catch (ClassNotFoundException cnfe) {
				logger.error(cnfe);
	        	
	        } finally {
	        	try {
	                if (update != null) {
	                	update.close();
	                }
	        	} catch (SQLException exceptSql) {
	    			logger.error(exceptSql);
	            }
	        	try {
	                if (connect != null){
	                    connect.close();
	                }
	            } catch (SQLException exceptSql) {
	    			logger.error(exceptSql);
	            }
	        }
			
			
		}
		
		
		return nextView;
	}

}
