import MySQLdb
import urllib, httplib

raw_params={'citeseer_id':'', 'fname':'', 'lname':'', 'mname':'', 'num_pubs':'', 'index':'authors'}

server, port = 'localhost', '8081'
base_url= 'http://'+server+':'+port+'/graph.uark/rest/'

header_form = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
header_json = {"Content-type": "#application/json:"}


#----------------------------------------------------

def open_connection():
   conn = httplib.HTTPConnection(server+':'+port)
   return conn
#----------------------------------------------------

def close_connection(conn):
   conn.close()
#----------------------------------------------------

def post_request(form, conn, action):   
   conn.request("POST", base_url+ action, urllib.urlencode(form), headers=header_form)
   response = conn.getresponse()
   print response.status, response.reason
   data = response.read()
   print data
   print_status(data)
   
#----------------------------------------------------



#----------------------------------------------------

def print_status(status):
   htm = open('status.htm','w+')      
   htm.write(status)
   htm.close()
#-----------------------testing zone----------------------

add_author ='nodes/add_author'
edit_node ='nodes/edit_author'
add_rel= 'nodes/add_relationship'


form_auth={'citeseer_id':'1011', 'fname':'nisa',
            'lname':'meribum', 'mname':'asd', 'num_pubs':'10', 'index':'authors'}

form_rel={'num_connections':'77','relationship':'coauthor','source_id':'114','target_id':'74'}

conn = open_connection()

#post_request(form_auth, conn, add_author)
post_request(form_rel, conn, add_rel)

close_connection(conn)

#----------------------------------------------------
def db_fetch():
   # Open database connection
   db = MySQLdb.connect("","","","" )

   # prepare a cursor object using cursor() method
   cursor = db.cursor()

   # Prepare SQL query to INSERT a record into the database.
   sql = "select name, cluster, count(*) as num_pubs from authors GROUP by cluster limit 3,5;" 
   print "successful"
   try:
      # Execute the SQL command
      cursor.execute(sql)
      # Fetch all the rows in a list of lists.
      results = cursor.fetchall()

      for row in results:
         name_parts = row[0].split()
         if(len(name_parts)>=2):
            raw_params['fname']=name_parts[0]
            raw_params['lname']=name_parts[-1]
            raw_params['mname']=' '.join(name_parts[1:-1])

         raw_params['citeseer_id'] = row[1]
         raw_params['citeseer_id'] = str(row[2])

         # Now print fetched result
         print raw_params
   except:
      print "Error: unable to fecth data"
   # disconnect from server
   db.close()
#----------------------------------------------------




print "done!"
