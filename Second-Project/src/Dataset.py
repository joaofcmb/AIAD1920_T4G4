

class Dataset:
    """
    Dataset class where all information about the
    the dataset will be stored and manipulated
    """
    stemmer = None  # Stemmer
    words = None  # Stop words list
    reviews = []  # Structure containing drug reviews
    evaluations = []  # Structure containing drug ratings
    dsdict = {'review': reviews, 'evaluation': evaluations}  # Dataset dictionary

    # ---------------------------------------------------
    #   Dataset class default constructor
    #       + Filename: File to be read
    # ---------------------------------------------------
    def __init__(self, filename):
        self.filename = filename

