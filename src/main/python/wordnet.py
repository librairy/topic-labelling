from nltk.corpus.reader.wordnet import WordNetError
from textblob.wordnet import Synset
light = Synset('light.n.01')
pollution = Synset('pollution.n.01')

words = ["sky","night","Dillingan","Airfield","Blue","Judaism","hipparcos","object"]
for x in words:
    try:
        y = Synset(x + '.n.01')
        print( x + ' [' + str(y.path_similarity(light)) + ',' + str(y.path_similarity(pollution)) +']')
    except WordNetError:
        print("Ooops! word not found in wordnet '" + x + "'")
