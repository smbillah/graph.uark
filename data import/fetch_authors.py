import MySQLdb
import urllib, httplib
import unicodedata


raw_params={'citeseer_id':'', 'fname':'', 'lname':'', 'mname':'', 'num_pubs':'', 'index':'authors'}

server, port = '130.184.102.110', '8080'
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
   return data.split()[0]
   #print_status(data)
   
#----------------------------------------------------



#----------------------------------------------------

def print_status(status):
   htm = open('status.htm','w+')      
   htm.write(status)
   htm.close()
#-----------------------testing zone----------------------


update_publication='nodes/update_publication'
search_author ='nodes/is_name_exists'
search_cluster ='nodes/is_cluster_exists'
add_author ='nodes/add_author'
edit_node ='nodes/edit_author'
add_rel= 'nodes/add_relationship'
get_author= 'nodes/{0}'



form_auth={'citeseer_id':'1011', 'fname':'nisa', 'lname':'meribum', 'mname':'asd', 'num_pubs':'10', 'index':'authors'}
form_cluster={'cluster':'1211'}
form_rel={'num_connections':'77','relationship':'coauthor','source_id':'11','target_id':'14'}

#conn = open_connection()
#post_request(form_auth, conn, add_author)
#post_request(form_rel, conn, add_rel)
#close_connection(conn)

#----------------------------------------------------

def get_str(s):   
   return unicodedata.normalize('NFKD', unicode("", 'utf_8')+s.strip()).encode('ascii','ignore')



def db_fetch():
   # Open database connection
   db = MySQLdb.connect(host="localhost",user="citeseer",passwd="",db="citeseerx",charset = "utf8", use_unicode = True )

   # prepare a cursor object using cursor() method
   cursor = db.cursor()

   # quey goes here
   query_paper = "select id, title, year from papers GROUP by cluster limit 2;" 
   query_all_author = "select name, cluster, count(*) from authors GROUP by cluster limit 3,10000"    
   query_author = "select name, cluster, count(*) as num_pubs from authors where paperid ='{0}' ";
   
   print "start data mining...."
   try:
      # Execute the SQL command
      cursor.execute("select name, cluster, count(*) from authors where cluster > 2 GROUP by cluster") #limit 2
      
      # Fetch all the rows in a list of lists.
      results = cursor.fetchall()
      conn = open_connection()
      
      for r in results:                  
         name_parts = r[0].strip().split()
         if(len(name_parts)>=2):
            raw_params['fname']= get_str(name_parts[0])
            raw_params['lname']= get_str(name_parts[-1])
            raw_params['mname']= get_str(' '.join(name_parts[1:-1]))
         else:
            raw_params['fname']= get_str(name_parts[0])
            raw_params['lname']= raw_params['fname']
            raw_params['mname']= ''

         raw_params['citeseer_id'] = str(r[1])
         raw_params['num_pubs'] = str(r[2])
         print "raw: ", raw_params
         
         form_cluster['cluster'] = raw_params['citeseer_id']
         id = post_request(form_cluster, conn, search_cluster)
         print "id: ", id

         # Now print fetched result 
         #print raw_params
         if int(id) == 0: 
            post_request(raw_params, conn, add_author)

      cursor.close()            
      close_connection(conn)
      

   except:
      print "Error: unable to fecth data"
   # disconnect from server
   db.close()
#----------------------------------------------------
db_fetch()
print "done!"
