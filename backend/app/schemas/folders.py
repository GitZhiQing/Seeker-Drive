from pydantic import BaseModel


class FolderBase(BaseModel):
    name: str
    parent_id: int


class FolderCreate(FolderBase):
    pass


class Folder(FolderBase):
    foid: int
    user_id: int

    class Config:
        from_attributes = True
