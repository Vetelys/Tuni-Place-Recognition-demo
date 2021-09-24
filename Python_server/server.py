import flask
import werkzeug
import random


app = flask.Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def handle_request():
    x = random.randint(0, 1200)
    y = random.randint(0, 1200)
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save(filename)
    return "Coordinates: {} {}".format(x, y)


app.run(host="0.0.0.0", port=5000, debug=True)