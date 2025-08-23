import tensorflow as tf
model = tf.keras.models.load_model('model_4gap.keras')
model.export('model')