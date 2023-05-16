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

  getFiles(page, size) {
    return http.get("/files?page=" + page + "&size=" + size);
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
