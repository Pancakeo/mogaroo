import httplib2
import sys
import re
import urllib.parse
import time

# Note: Requires the httplib2 library: http://code.google.com/p/httplib2/
# I forget how to install it, but it's probably easy!

ENCODING = "utf-8"
LOGIN_HOST = "https://weblogin.washington.edu"
CACHE_FOLDER = "cache"
USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.107 Safari/535.1"
MULTIPART_POST = "application/x-www-form-urlencoded"
NL = "\n"

DEF_HEADERS = {"User-Agent" : USER_AGENT}

PUB_COOKIE = None
USER_NAME = None
PASS_WORD = None
SLEEP_TIME = 666

URL_TO_CHECK = "https://sdb.admin.washington.edu/timeschd/uwnetid/tsstat.asp?QTRYR=AUT+2011&CURRIC=MATH"
FIND_CLASS = "16262"
#FIND_CLASS = "16257"

# Login.
def doLogin():
	#print("Attempting to login...")
	
	response, content = http_client.request(LOGIN_HOST, headers = DEF_HEADERS)
	postData = {"user" : USER_NAME, "pass" : PASS_WORD}
	
	res_as_string = content.decode(ENCODING)
	
	# Grab hidden fields, which are used in authentication and direction.
	for line in res_as_string.split(NL):
		if "input type=\"hidden\"" in line:
			m = re.search("(?i)input type=\"hidden\" name=\"(.*)\" value=\"(.*)\"", line)
			
			if m is not None:
				key = m.group(1)
				value = m.group(2)
				postData[key] = value
	
	# Login.
	doPost(LOGIN_HOST, postData)
		
def doPost(path, postData):
	global PUB_COOKIE
	
	#print("Sending POST to " + path)
	
	postHeaders = {"User-Agent" : USER_AGENT}
	postHeaders["Content-Type"] = MULTIPART_POST
	
	if (PUB_COOKIE != None):
		postHeaders["Cookie"] = PUB_COOKIE
	
	response, content = http_client.request(path, "POST", headers = postHeaders, body=urllib.parse.urlencode(postData))

	if "set-cookie" in response:
		PUB_COOKIE = response["set-cookie"]
	
	res_as_string = (content.decode(ENCODING))
	
	# Oh man this is bad!
	if "Login failed.  Please re-enter" in res_as_string:
		quit("ERROR: Login failed with username " + USER_NAME + NL)
	
	#if "location" in response:
		#print("Found location header: " + response["location"])
			
	return res_as_string
		
def doAbsoluteGet(path):
	#print("Sending GET to " + path)
	someHeaders = {"User-Agent" : USER_AGENT}
	someHeaders["Cookie"] = PUB_COOKIE
	response, content = http_client.request(path, "GET", headers = someHeaders)
	
	# Need to authenticate...
	res_as_string = content.decode(ENCODING)
	postData = {}

	for line in res_as_string.split(NL):
		# The name isn't quoted.
		if "input type=hidden" in line:
			m = re.search("(?i)input type=hidden name=(.*) value=\"(.*)\"", line)
			
			if m is not None:
				key = m.group(1)
				value = m.group(2)
				postData[key] = value
	
	res = doPost(LOGIN_HOST, postData)
	
	# Post more stuff...
	postData = {}
	for line in res.split(NL):
		if "input type=hidden" in line:
			m = re.search("(?i)input type=hidden name=(.*) value=\"(.*)\"", line)
				
			if m is not None:
				key = m.group(1)
				key = key.replace("\"", "")
				value = m.group(2)
				postData[key] = value
	
	# Fuck you pub cookie
	res = doPost("https://sdb.admin.washington.edu/relay.pubcookie3?appsrvid=sdb.admin.washington.edu", postData)

	# Yarrrgh, the final request.... which is also the first request, but with a new cookie.
	someHeaders = {"User-Agent" : USER_AGENT}
	someHeaders["Cookie"] = PUB_COOKIE
	response, content = http_client.request(path, "GET", headers = someHeaders)
	res_as_string = content.decode(ENCODING)
	
	return res_as_string

#-------------------

def check():
	global http_client
	http_client = httplib2.Http(CACHE_FOLDER)
	print("Checking...")
	doLogin()
	finalTable = doAbsoluteGet(URL_TO_CHECK)

	# ---- Warning: The code below is terrible! The tables should be parsed into an XML object and then accessed via XPath... or some other STRUCTURED approach.

	# This parsing code is really bad.
	badBad = finalTable.find(FIND_CLASS)
	nextBad = finalTable.find("<A HREF", badBad + 1)
	
	if (badBad == -1):
		print(str(FIND_CLASS) + " was not found")
		
	else:
		subStr = finalTable[badBad:nextBad]
		
		index = 0
		colMap = {0 : "Link", 1 : "Name", 2 : "Section", 3 : "Type", 4 : "Title", 5 : "Enrolled", 6 : "Limit", 7 : "Room Capacity", 8 : "Space Available", 9 : "Denied Requests", 10 : "Notes"}
		
		for entry in subStr.split("<T"):
			m = re.search("(?i).*>(.*)</t[dh]>", entry)
			
			if m is not None:
				data = m.group(1).strip()
				if colMap[index] == "Space Available" and data != "0":
					print("SPACE AVAILABLE!!!!: " + data)
				elif colMap[index] == "Space Available" and data == "0":
					print("No space available.")
				
				#print(colMap[index] + ": " + data)
				index += 1

# Main code
if len(sys.argv) < 3:
	quit("Usage: " + sys.argv[0] + " username password" + NL)
else:
	# This should probably be in something.
	# Java GUI!
	USER_NAME = sys.argv[1]
	PASS_WORD = sys.argv[2]
	http_client = None
	
	while (True):
		check()
		print("Sleeping for " + str(SLEEP_TIME) + "s.")
		time.sleep(SLEEP_TIME)