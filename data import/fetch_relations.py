#!/usr/bin/env python
#-*- coding: utf-8 -*-

import MySQLdb
import urllib, httplib
import json
import itertools
import unicodedata

#raw_params={'citeseer_id':'', 'fname':'', 'lname':'', 'mname':'', 'num_pubs':'', 'index':'authors'}

#server, port = 'localhost', '8081'
server, port = '130.184.102.110', '8080'
base_url= 'http://'+server+':'+port+'/graph.uark/rest/'

header_form = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
header_json = {"Content-type": "application/json","Accept": "application/json"}


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
   #print response.status, response.reason
   data = response.read()
   #print data   
   #print_status(data)
   return data.split()[0]
   
#----------------------------------------------------
def get_request(id, conn, action):   
   conn.request("GET", base_url+ action.format(id),  headers=header_json)
   response = conn.getresponse()
   #print response.status, response.reason
   data = response.read()
   #print data
   #print_status(data)
   return data



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


form_update={"id":'',"num_pubs":''}
form_auth={'citeseer_id':'1011', 'fname':'', 'lname':'', 'mname':'asd', 'num_pubs':'10', 'index':'authors'}
form_search={'fname':'nisa', 'lname':'meribum'}
form_cluster={'cluster':'1211'}
form_rel={'num_connections':'1','relationship':'coauthor','source_id':'1','target_id':'2'}

#conn = open_connection()
#post_request(form_auth, conn, add_author)
#post_request(form_rel, conn, add_rel)
#author= json.loads(get_author(1, conn, get_author))
#print author['fname'], author['lname']
#close_connection(conn)

#----------------------------------------------------
# quey goes here
query_paper = "select id, title, year from papers GROUP by cluster limit 2;" 
query_all_author = "select name, cluster, count(*) from authors GROUP by cluster limit 3,10000; "    
query_single_author = "select name, cluster, count(*) from authors where cluster = {0} GROUP by cluster";
query_author = "select name, cluster, count(*) as num_pubs from authors where paperid ='{0}' ";
query_paper_auth = "select name, cluster from authors where paperid ='{0}' ";
query_auth_paper= "select paperid from authors where cluster={0}"


#----------------------------------------------------

def get_str(s):   
   return unicodedata.normalize('NFKD', unicode("", 'utf_8')+s.strip()).encode('ascii','ignore')

   
def create_uncat_author(conn, fname, lname, cluster):
   form_search['fname']= get_str(fname)
   form_search['lname']= get_str(lname)
   id = post_request(form_search, conn, search_author)
   
   if(int(id)==0):#not exist in name index
      form_auth['fname']= form_search['fname']
      form_auth['lname']= form_search['lname']
      form_auth['mname']= ''
      form_auth['citeseer_id'] = cluster                        
      form_auth['num_pubs'] = 1
      id = post_request(form_auth, conn, add_author)
   else:
      #update 
      form_update["id"]= id
      form_update["num_pubs"]= '1'
      post_request(form_update, conn, update_publication)
   return id
   
   

def create_author(db, conn, cluster):
   cursor = db.cursor()                  
   cursor.execute("select name, cluster, count(*) from authors where cluster = {0} GROUP by cluster".format(cluster))                  
   results = cursor.fetchall()

   for r in results:                  
      name_parts = r[0].split()
      if(len(name_parts)>=2):
         form_auth['fname']= get_str(name_parts[0])
         form_auth['lname']= get_str( name_parts[-1])
         form_auth['mname']= get_str( ' '.join(name_parts[1:-1]))
      else:
         form_auth['fname']= get_str(name_parts[0])
         form_auth['lname']= form_auth['fname']
         form_auth['mname']= ''
         
      form_auth['citeseer_id'] = str(r[1])                        
      form_auth['num_pubs'] = str(r[2])
      break   
   cursor.close()                  
   id = post_request(form_auth, conn, add_author)
   return id
#-----------------------------------------------------------------------   

def db_fetch():

   # Open database connection
   db = MySQLdb.connect(host="localhost",user="citeseer",passwd="",db="citeseerx",charset = "utf8", use_unicode = True )

   # prepare a cursor object using cursor() method   
   print "start data mining...."
   try:
      conn = open_connection()
      
      for id in range(2, 132155):#9982):
                 
         author= json.loads(get_request(id, conn, get_author))
         #print "ok"
         #print "author: ", author
         
         # get all the papers written by this author
         cursor = db.cursor()
         cursor.execute("select paperid from authors where cluster={0}".format(author['citeseer_id']))
         
         # Fetch all papers 
         papers=[] 
         results = cursor.fetchall()         
         for p in results:
            papers.append(p[0])
            
         cursor.close()
         #print "papers: ", papers         
         
         #iterate through each of the papers                 
         for paper in papers:
            cursor = db.cursor()
            cursor.execute("select name, cluster from authors where paperid ='{0}'".format(paper))
            results = cursor.fetchall()         
            
            #get coauthors of this paper
            coauthors=[]                        
            for c in results:
               coauthors.append(c)               
            cursor.close()            
            #print "paper {0} has coauthors: ".format(paper), coauthors
            
            #get node_id of each coauthors
            node_id=[]
            for a in coauthors:               
               #print "OK1"
               name_parts = a[0].strip().split()
               #print "name_parts: ", name_parts                              
               form_search['fname'] = get_str(name_parts[0]).lower()
               form_search['lname'] = get_str(name_parts[-1]).lower()               
               #print form_search
               
               id = post_request(form_search, conn, search_author)
               #print "id: ", id, form_search
               
               if int(id) == 0: #not exits in node db
                  #print "create new author"
                  if int(a[1]) < 3: # for uncategorized authors
                     id = create_uncat_author(conn, form_search['fname'], form_search['lname'], a[1])                     
                  else:
                     id = create_author(db, conn, a[1])

                  #print "new author created id: ", id            
               # end new author
               
               if int(id)>0 and id not in node_id:
                  node_id.append(id)
               #print "OK2"
            #end ids
            #print "ids", node_id
            
            #permutate authors' id
            for (x,y) in itertools.combinations(node_id,2):               
               form_rel['source_id'] = x
               form_rel['target_id'] = y  
               #print "({0}, {1})".format(x,y)
               post_request(form_rel, conn, add_rel)
               

         # end single paper update
      # end all authors update   

      close_connection(conn)

   except:
      print "Error occurs!"
   # disconnect from server
   db.close()
   
#----------------------------------------------------
db_fetch()
print "done!"
