import flask
import werkzeug
import random
import os


app = flask.Flask(__name__)


@app.route('/upload/<response_param>', methods=['GET', 'POST'])
def upload_image(response_param):
    try:
        imagefile = flask.request.files['image']
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        print("\nReceived image File name : " + imagefile.filename)
        imagefile.save(filename)
        if response_param == 'image':
            response_filename = "map.jpg"
            if os.path.exists(response_filename):
                return flask.send_file(response_filename, mimetype='image/png')
            else:
                return "", 204
        elif response_param == 'text':
            return "Coordinates", 200
    except Exception as err:
        print("Error: ")
        print(err)
        return "Error, request did not contain an image."


@app.route('/result', methods=['GET', 'POST'])
def set_result():
    try:
        img_filename = flask.request.form['image_name']
        result = flask.request.form['result']

        if result == "Correct":
            dir = os.getcwd()+"\\Correct\\"

        elif result == "False":
            dir = os.getcwd()+"\\False\\"

        if not os.path.exists(dir):
            os.makedirs(dir)
        # move image to folder correct/false
        os.rename(os.getcwd()+"\\"+img_filename, dir+img_filename)
        return " ", 200

    except Exception as err:
        print("Error: ")
        print(err)
        return "Error, request did not contain needed data", 400


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)