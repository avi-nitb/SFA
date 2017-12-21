package in.etaminepgg.sfa.Utilities;


import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static in.etaminepgg.sfa.Activities.LoginActivity.uploadToURL;
import static in.etaminepgg.sfa.Utilities.Constants.appSpecificDirectoryPath;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

public class MyDb
{
    // constants
    public static final int ERROR = -1;
    public static final int PARSER_OUTSIDE = 10;
    public static final int PARSER_INSIDE = 11;
    public static final int PARSER_TBLHEAD = 12;


    // SCOPEDB Implementation
    public static final int MAX_DB_COUNT = 100;
    public static final int MAX_DB_FILE_COUNT = 100;
    public static final int MAX_DB_ROW_COUNT = 100;
    public static final int MAX_DBS = 500000;
    public static String[] dbFiles = new String[MAX_DB_FILE_COUNT]; // max 100 database files
    public static String[] dbNames = new String[MAX_DB_COUNT]; // max 100 database tables
    public static String[][] colNames = new String[MAX_DB_COUNT][];
    public static String[][][] tbl = new String[MAX_DB_COUNT][][];
    public static int curDbCount = 0;
    //public static int dbList[] = new int[MAX_DBS];
    public static SQLiteDatabase dbHandles[] = new SQLiteDatabase[MAX_DBS];
    //public static int dbCount = 0;
    public static int nextDbId = 1;


    public static boolean isEmpty(String s)
    {
        if(s.trim().equals(""))
        {
            return true;
        }
        return false;
    }


    public static boolean isComment(String s)
    {
        if(s.trim().matches("^\\s*#.*"))
        {
            return true;
        }
        return false;
    }

    public static boolean isValidTblName(String s)
    {
        if(s.trim().matches("^[^`]+$"))
        {
            return true;
        }
        return false;
    }

    public static boolean isValidTblColNames(String s)
    {
        if(s.trim().matches("^[^`]+$"))
        {
            return true;
        }
        String[] ar = s.trim().split("`");
        for(int i = 0; i < ar.length; i++)
        {
            if(ar[i].matches(".*\\s+.*"))
            {
                return false; // white spaces are not allowed in column names
            }
        }
        return true;
    }

    public static String escapeDoubleQuotes(String s)
    {
        String ret = s.replace("\"", "\"\"");
        return (ret);
    }

    // sqlite database
    public static int openDatabase(String dbPath)
    {
        try
        {
            if(nextDbId >= MAX_DBS)
            {
                return (-1);
            }
            //dbHandles[nextDbId] = SQLiteDatabase.openOrCreateDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS|SQLiteDatabase.OPEN_READONLY);
            dbHandles[nextDbId] = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
            nextDbId++;
            return (nextDbId - 1);
        }
        catch(SQLException e)
        {

            MyUi.popUp("Unable to create " + dbPath + " : " + e.toString());
            //e.printStackTrace();
        }
        return (ERROR);
    }

    public static int closeDb(int dbh)
    {
        if(dbh >= MAX_DBS)
        {
            return (-1);
        }
        if(dbHandles[dbh] == null)
        {
            return (-1);
        }
        try
        {
            dbHandles[dbh].close();
            return (1);
        }
        catch(Exception e)
        {
            MyUi.popUp("Unable to close Db " + dbh);
        }
        return (-1);
    }

    /*// sqlite database
    public static int openDatabase(String dbPath)
    {
        try
        {
            FileFunctions.decodeFile("KEY_Eta@13pGG-Sam#14&ngpr*Bnglr", dbFileFullPath, LoginActivity.DbFileName, appSpecificDirectoryPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            if (nextDbId >= MAX_DBS)
            {
                return (-1);
            }
            dbHandles[nextDbId] = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
            nextDbId++;
            return (nextDbId - 1);
        }
        catch (SQLException e)
        {

            MyUi.popUp("Unable to create " + dbPath + " : " + e.toString());
            //e.printStackTrace();
        }
        return (ERROR);
    }

    public static int closeDb(int dbh)
    {
        if (dbh >= MAX_DBS)
        {
            return (-1);
        }
        if (dbHandles[dbh] == null)
        {
            return (-1);
        }

        try
        {
            FileFunctions.decodeFile("KEY_Eta@13pGG-Sam#14&ngpr*Bnglr", dbFileFullPath, LoginActivity.DbFileName, appSpecificDirectoryPath);
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }

        try
        {
            FileFunctions.encodeFile("KEY_Eta@13pGG-Sam#14&ngpr*Bnglr", dbFileFullPath, LoginActivity.DbFileName, appSpecificDirectoryPath);
        }
        catch (Exception e)
        {
//            e.printStackTrace();
        }

        try
        {
            dbHandles[dbh].close();
            return (1);
        }
        catch (Exception e)
        {
            MyUi.popUp("Unable to close Db " + dbh);
        }
        return (-1);
    }*/

    public static SQLiteDatabase getDbHandle(int dbh)
    {
        return (dbHandles[dbh]);
    }

    public static int runQuery(int dbh, String q)
    {
        if(dbh >= MAX_DBS)
        {
            return (-1);
        }
        if(dbHandles[dbh] == null)
        {
            return (-1);
        }
        try
        {
            dbHandles[dbh].execSQL(q);
            return (1);
        }
        catch(Exception e)
        {
            MyUi.popUp("Unable to run query " + q + " on " + dbh);
        }
        return (-1);
    }

    // Add tables from the given scopedb file into the sqldb
    public static int loadScopeDbFile(String fileName, int dbi, int overWriteFlag)
    {
        int ret = 1; // return value - by default success
        SQLiteDatabase dbhandle = getDbHandle(dbi);
        int lineno = 0;
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(fileName));
        }
        catch(IOException e)
        {
            MyUi.popUp("loadScopeDb : Unable to open file " + fileName + " : " + e.toString());
            return (ERROR);
        }


        String line = null;
        String curTblName = null;
        String curTblFields[] = null;
        int curTblRows = 0;
        int state = PARSER_OUTSIDE;
        try
        {

            while((line = br.readLine()) != null)
            {
                lineno++;
                // skip comments
                if(isComment(line) || isEmpty(line))
                {
                    continue;
                }
                if(state == PARSER_OUTSIDE)
                {
                    if(!isValidTblName(line))
                    {
                        MyUi.popUp("Unexpected table head in line " + lineno + " : " + line);
                        closeDb(dbi);
                        br.close();
                        return (ERROR);
                    }
                    state = PARSER_TBLHEAD;
                    curTblName = line.trim();
                    // All the dashes must be replaced with underscores
                    curTblName = curTblName.replace('-', '_');
                    curTblRows = 0;
                    //MyUi.popUpQuick("Loading table " + curTblName);
                    continue;
                }

                if(state == PARSER_TBLHEAD)
                {
                    if(!isValidTblColNames(line))
                    {
                        MyUi.popUp("Invalid field names specification on line " + lineno + " : " + line);
                        closeDb(dbi);
                        br.close();
                        return (ERROR);
                    }
                    line = line.trim();
                    curTblFields = line.split("`");

                    // Delete the table if needed
                    if(overWriteFlag == 1)
                    {
                        runQuery(dbi, "DROP TABLE IF EXISTS " + curTblName);
                        String query = "CREATE TABLE " + curTblName + " ( ";
                        for(int i = 0; i < curTblFields.length; i++)
                        {
                            query += curTblFields[i] + " TEXT";
                            if(i != curTblFields.length - 1)
                            {
                                query += ", ";
                            }
                        }
                        query += " )";
                        runQuery(dbi, query);
                    }


                    state = PARSER_INSIDE;
//					MyUi.popUp("Fields = " + query);
                    continue;
                }
                if(state == PARSER_INSIDE)
                {
//					MyUi.popUpQuick("data line 0 = " + line);
                    if(line.trim().compareTo(".") == 0)
                    {
                        state = PARSER_OUTSIDE;
                        curTblName = "";
                        continue;
                    }
//					MyUi.popUpQuick("data line 01 = " + line);
                    String[] vals = line.trim().split("`", -1);
//					MyUi.popUpQuick("data line 02 = " + line);

//					MyUi.popUp("data line 1 = " + line);
                    if(vals.length != curTblFields.length)
                    {
                        MyUi.popUp("Column counts mismatch. Expected = " + curTblFields.length + ", seen = " + vals.length + " on line " + lineno);
                        closeDb(dbi);
                        br.close();
                        return (ERROR);
                    }
//					MyUi.popUpQuick ("data line 2 = " + line);
                    String query = "INSERT INTO " + curTblName + "(";
//					MyUi.popUpQuick(" one = " + query);
                    for(int i = 0; i < curTblFields.length; i++)
                    {
                        query += curTblFields[i];
                        if(i != curTblFields.length - 1)
                        {
                            query += ", ";
                        }
                    }

                    query += " ) VALUES (";
                    for(int i = 0; i < vals.length; i++)
                    {
                        query += "\"" + escapeDoubleQuotes(vals[i]) + "\"";
                        if(i != vals.length - 1)
                        {
                            query += ", ";
                        }
                    }
                    query += " )";
                    //MyUi.popUpQuick("query = " + query);
                    try
                    {
                        runQuery(dbi, query);
                    }
                    catch(Exception e)
                    {
                        MyUi.popUp("Crash : " + query);
                    }
                }
            }

            br.close();
        }
        catch(IOException e)
        {
            MyUi.popUp("Unable to read DB file " + fileName);
            //closeDb( DBI );
            return (ERROR);
        }

        //MyUi.popUp( "Data loaded.");


        return (ret);
    }


    // returns the handle to the db
    public static int loadScopeDbOld(String fileName, String dbFileName) // full path
    {
        //if(FileFunctions.fileExists(fileName)) FileFunctions.deleteFile(fileName);

        // make sure the no. of DBs are less than max
        if(curDbCount >= MAX_DB_COUNT)
        {
            return (ERROR);
        }
        int curDbIndex = curDbCount; // starts from 0


        int ret = 0;
        int lineno = 0;
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(fileName));
        }
        catch(IOException e)
        {
            MyUi.popUp("loadScopeDb : Unable to open file " + fileName + " : " + e.toString());
            return (ERROR);
            //You'll need to add proper error handling here
        }


//		if(FileFunctions.fileExists(fileName)) FileFunctions.deleteFile(dbFileName);
        int dbi = openDatabase(dbFileName);
        SQLiteDatabase dbh = getDbHandle(dbi);


        String line = null;
        String curTblName = null;
        String curTblFields[] = null;
        int curTblRows = 0;
        int state = PARSER_OUTSIDE;
        try
        {

            while((line = br.readLine()) != null)
            {
                lineno++;
                // skip comments
                if(isComment(line) || isEmpty(line))
                {
                    continue;
                }
                if(state == PARSER_OUTSIDE)
                {
                    if(!isValidTblName(line))
                    {
                        MyUi.popUp("Unexpected table head in line " + lineno + " : " + line);
                        closeDb(dbi);
                        br.close();
                        return (ERROR);
                    }
                    state = PARSER_TBLHEAD;
                    curTblName = line.trim();
                    dbNames[curDbIndex] = curTblName;
                    dbFiles[curDbIndex] = FileFunctions.getNameFromFileName(fileName);
                    curTblRows = 0;
                    //MyUi.popUp("Table name = " + curTblName);
                    continue;

                }
                if(state == PARSER_TBLHEAD)
                {
                    if(!isValidTblColNames(line))
                    {
                        MyUi.popUp("Invalid field names specification on line " + lineno + " : " + line);
                        closeDb(dbi);
                        br.close();
                        return (ERROR);
                    }
                    line = line.trim();
                    curTblFields = line.split("`");
                    colNames[curDbIndex] = curTblFields;
                    String query = "CREATE TABLE " + curTblName + " ( ";
                    for(int i = 0; i < curTblFields.length; i++)
                    {
                        query += curTblFields[i] + " TEXT";
                        if(i != curTblFields.length - 1)
                        {
                            query += ", ";
                        }
                    }
                    query += " )";
                    runQuery(dbi, query);

                    state = PARSER_INSIDE;
//					MyUi.popUp("Fields = " + query);
                    continue;
                }
                if(state == PARSER_INSIDE)
                {
//					MyUi.popUpQuick("data line 0 = " + line);
                    if(line.trim().compareTo(".") == 0)
                    {
                        state = PARSER_OUTSIDE;
                        curTblName = "";
                        continue;
                    }
//					MyUi.popUpQuick("data line 01 = " + line);
                    String[] vals = line.trim().split("`");
//					MyUi.popUpQuick("data line 02 = " + line);

//					MyUi.popUp("data line 1 = " + line);
                    if(vals.length != curTblFields.length)
                    {
                        MyUi.popUp("Column counts mismatch. Expected = " + curTblFields.length + ", seen = " + vals.length + " on line " + lineno);
                        closeDb(dbi);
                        br.close();
                        return (ERROR);
                    }
//					MyUi.popUpQuick ("data line 2 = " + line);
                    String query = "INSERT INTO " + curTblName + "(";
//					MyUi.popUpQuick(" one = " + query);
                    for(int i = 0; i < curTblFields.length; i++)
                    {
                        query += curTblFields[i];
                        if(i != curTblFields.length - 1)
                        {
                            query += ", ";
                        }
                    }

                    query += " ) VALUES (";
                    for(int i = 0; i < vals.length; i++)
                    {
                        query += "\"" + vals[i] + "\"";
                        if(i != vals.length - 1)
                        {
                            query += ", ";
                        }
                    }
                    query += " )";
//					MyUi.popUpQuick("query = " + query);
                    runQuery(dbi, query);
                }
            }

            br.close();
        }
        catch(IOException e)
        {
            MyUi.popUp("Unable to read text file " + fileName);
            closeDb(dbi);
            return (ERROR);
            //You'll need to add proper error handling here
        }

//		MyUi.popUp( "Data reloaded.");
        closeDb(dbi);
        return (dbi);


    }

	/*
    public static int loadScopeDbUnused( String fileName ) // full path
	{
		if(FileFunctions.fileExists(fileName)) FileFunctions.deleteFile(fileName);


		// make sure the no. of DBs are less than max
		if( curDbCount >= MAX_DB_COUNT )
		{
			return(ERROR);
		}
		int curDbIndex = curDbCount; // starts from 0


		int ret = 0;
		int lineno = 0;
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(fileName));
		}
		catch (IOException e) {
			MyUi.popUp("loadScopeDb : Unable to open file " + fileName + " : " + e.toString());
			return( ERROR );
			//You'll need to add proper error handling here
		}

		String line = null;
		String curTblName = null;
		String curTblFields[] = null;
		int curTblRows = 0;
		int state = PARSER_OUTSIDE;
		try {

			while ((line = br.readLine()) != null) {
				lineno++;
				// skip comments
				if( isComment( line ) || isEmpty( line )) { continue; }
				if( state == PARSER_OUTSIDE )
				{
					if( !isValidTblName( line ) ) { MyUi.popUp("Unexpected table head in line " + lineno); return( ERROR ); }
					state = PARSER_TBLHEAD;
					curTblName = line.trim();
					dbNames[curDbIndex] = curTblName;
					dbFiles[curDbIndex] = FileFunctions.getNameFromFileName(fileName);
					curTblRows = 0;
					continue;

				}
				if( state == PARSER_TBLHEAD)
				{
					if( !isValidTblColNames( line ) )
					{
						MyUi.popUp("Invalid field names specification on line " + lineno );
						return( ERROR );
					}
					line = line.trim();
					curTblFields = line.split("`");
					colNames[curDbIndex] = curTblFields;
					tbl[curDbIndex] = new String[MAX_ROW_COUNT][];
					state = PARSER_INSIDE;
					continue;
				}
				if( state == PARSER_INSIDE)
				{
					String[] vals = line.trim().split("`");
					if( vals.length != curTblFields.length)
					{
						MyUi.popUp("Column counts mismatc. Expected = " + curTblFields.length + ", seen = " + vals.length() + " on line " + lineno);
						return( ERROR );
					}
					curTblRows++;
					tbl[curDbIndex].)
				}

			}
		}
		catch (IOException e) {
			//You'll need to add proper error handling here
		}


		return( ret );
	}
	 */

    public static SQLiteDatabase openDatabaseForReading(String dbFile)
    {
        SQLiteDatabase dbh = null;
        try
        {
            if(!FileFunctions.fileExists(dbFile))
            {
                MyUi.popUp("db file " + dbFile + " does not exist !");
                return (null);
            }
            dbh = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
        }
        catch(Exception e)
        {
            MyUi.popUp("Unable to open plan database. Operation Aborted : " + e.toString());
            return (null);
        }
        if(!dbh.isOpen())
        {
            MyUi.popUp("Plan database not available.");
            return (null);
        }
        return (dbh);
    }


    /*
    public static int openDatabaseForReadingOld( String dbPath ) {
        try {
            if( nextDbId >= MAX_DBS) return( -1 );
            //dbHandles[nextDbId] = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS|SQLiteDatabase.OPEN_READONLY);
            SQLiteDatabase nid =  SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS|SQLiteDatabase.OPEN_READONLY);
            dbHandles[nextDbId] = nid;
            nextDbId++;
            return( nextDbId - 1 );

        } catch (SQLException e) {
            MyUi.popUp( "Unable to open " + dbPath + " : " + e.toString() );
            e.printStackTrace();
        }
        return( -1 );
    }
*/
    public static boolean uploadData(int dbh, String perId, String completeData)
    {

        try
        {

            SimpleDateFormat fsdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String DateTime = fsdf.format(new Date());

            File src_file = new File(dbFileFullPath);
            long length1 = src_file.length();

            String fprefix1 = "dirserv_" + perId + "_" + DateTime + "_" + length1;

            String newName1 = appSpecificDirectoryPath + File.separator + fprefix1 + ".reddb";

            if(FileFunctions.fileExists(newName1))
            {
                FileFunctions.deleteFile(newName1);
            }

            FileFunctions.writeTextFile(completeData, newName1);

            File dest_file1 = new File(newName1);

            long length = dest_file1.length();
            String fprefix = "dirserv_" + perId + "_" + DateTime + "_" + length;
            String newName = appSpecificDirectoryPath + File.separator + fprefix + ".reddb";

            File dest_file = new File(newName);

            FileFunctions.copy(dest_file1, dest_file);

            FileFunctions.deleteFile(newName1);

            try
            {
                int ret = FileFunctions.uploadFile(newName, uploadToURL);

                Log.d("newName", newName + " Server Link " + uploadToURL);

                Log.d("Both file Size :", "Original File : " + length + " New File : " + dest_file.length() + " Respnce : " + ret);

                if(ret == length)
                {
                    Log.i("Data Uploaded : ", "Data Uploaded Successfully and Replyed Lenght is : " + ret);

                    FileFunctions.deleteFile(newName);
//                    closeDb( surveyDbi );
                    return (true);
                }
                else
                {
                    Log.i("Error on Data Upload : ", "Error uploading survey data. Check network connection and try again.");
                    return (false);
                }
            }
            catch(Exception e)
            {
//                    MyUi.popupOk("Unable to upload survey data : " + e.getMessage());

                Log.i("Error on Data Upload : ", "" + e.getMessage());

//                closeDb( surveyDbi );
                return (false);
            }


//            closeDb( DBH );
//            return( false );
        }
        catch(Exception e)
        {
            Log.i("Error on Data Upload : ", "" + e.getMessage());

            return (false);
        }

//        return (false);
    }


}
