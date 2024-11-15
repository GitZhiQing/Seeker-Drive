from pydantic import BaseModel


class FileBase(BaseModel):
    name: str
    size: int
    type: str
    status: int
    hash: str
    folder_id: int
    user_id: int


class FileCreate(FileBase):
    pass


class File(FileBase):
    fid: int
    upload_time: str

    class Config:
        from_attributes = True
