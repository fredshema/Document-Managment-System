import React, { useEffect, useState } from "react";
import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import UploadFiles from "./components/UploadFilesComponent";
import Login from "./components/LoginComponent";
import Register from "./components/RegisterComponent";

function App() {
  const [token, setToken] = useState(null);
  const [isLogin, setIsLogin] = useState(true);
  const [show, setShow] = useState(false);

  useEffect(() => {
    localStorage.getItem("token")
      ? setToken(localStorage.getItem("token"))
      : setToken(null);
    setShow(true);
  }, []);

  const onLogout = () => {
    localStorage.clear();
    setToken(null);
    window.location.reload();
  };

  return (
    <div>
      <nav className="navbar navbar-dark bg-dark">
        <div className="btn-group mx-auto">
          <a href="/" className="text-white h5">
            File Managment System
          </a>
        </div>
        {token && (
          <button
            className="btn btn-light btn-sm float-right"
            onClick={onLogout}
          >
            Logout
          </button>
        )}
      </nav>
      {show && (
        <div className="container py-5">
          {!token && isLogin && <Login setIsLogin={setIsLogin} />}
          {!token && !isLogin && <Register setIsLogin={setIsLogin} />}
          {token && <UploadFiles />}
        </div>
      )}
    </div>
  );
}

export default App;
