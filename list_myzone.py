from pprint import pprint
from googleapiclient import discovery
from oauth2client.client import GoogleCredentials
import sys

credentials = GoogleCredentials.get_application_default()
service = discovery.build('compute', 'v1', credentials=credentials)

# Project ID for this request.
project = sys.argv[1]

def list_myzones():
    request = service.zones().list(project=project)
    
    while request is not None:
        response = request.execute()

    for zone in response['items']:
        pprint(zone['name'])

    request = service.zones().list_next(previous_request=request, previous_response=response)

list_myzones()
