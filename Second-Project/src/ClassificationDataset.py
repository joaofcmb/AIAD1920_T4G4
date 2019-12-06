"""
 Import Modules
"""
from src.Dataset import Dataset


class ClassificationDataset(Dataset):
    """
    Classification dataset class where all information about the
    the classification dataset will be stored and manipulated
    """

    def __init__(self, filename):
        super().__init__(filename)
