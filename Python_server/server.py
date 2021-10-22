import flask
import werkzeug
import random
import os


app = flask.Flask(__name__)


@app.route('/upload', methods=['GET', 'POST'])
def upload_image():
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

@app.route('/result', methods=['GET', 'POST'])
def set_result():
    try:
        print(flask.request.form)
        img_filename = flask.request.form['image_name']
        result = flask.request.form['result']
        if result == "Correct":
            os.rename(os.getcwd()+"\\"+img_filename, os.getcwd()+"\\Correct\\"+img_filename)
        elif result == "False":
            os.rename(os.getcwd()+"\\"+img_filename, os.getcwd()+"\\False\\"+img_filename)
    except Exception as err:
        print("Error: ")
        print(err)
        return "Error, request did not contain needed data"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)