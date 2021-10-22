import flask
import werkzeug
import random
import os


app = flask.Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def handle_request():
    try:
        imagefile = flask.request.files['image']
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        print("\nReceived image File name : " + imagefile.filename)
        imagefile.save(filename)
        response_filename = "map.jpg"
        success = True
        if(success):
            return flask.send_file(response_filename, mimetype='image/png')
        else:
            return "", 204
    except Exception as err:
        print("Error: ")
        print(err)
        return "Error, request did not contain an image."


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)