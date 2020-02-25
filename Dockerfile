FROM gcr.io/google.com/cloudsdktool/cloud-sdk

ENV GOOGLE_APPLICATION_CREDENTIALS=/tmp/mygcp-env.json

RUN pip3 install --upgrade google-api-python-client oauth2client

WORKDIR /app

COPY mygcp-env.json /tmp/

COPY list_myzone.py /app/

ENTRYPOINT [ "python3","/app/list_myzone.py" ]
