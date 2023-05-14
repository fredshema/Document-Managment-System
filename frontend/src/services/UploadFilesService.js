import http from "../Common";

class UploadFilesService {
  upload(file, onUploadProgress) {
    let formData = new FormData();

    formData.append("file", file);

    return http.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      onUploadProgress,
    });
  }

  getFiles() {
    return http.get("/files");
  }

  deleteFile(id) {
    return http.delete(`/delete/${id}`);
  }

  updateFile(id, name) {
    return http.put(`/update/${id}`, { name });
  }

  searchFile(filename) {
    return http.get(`/search/${filename}`);
  }

  syncFiles() {
    return http.get("/sync");
  }
}

export default new UploadFilesService();
