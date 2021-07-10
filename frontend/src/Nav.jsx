import React from 'react'
import useSWR from "swr";

import styles from './Nav.module.css'

function Nav() {
  let {data: user} = useSWR("/api/v1/user");

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-3">
      <div className="container">
        <a className="navbar-brand" href="#">IMDB Top 250 Tracker</a>
        {!!user?.name && <span className={styles.navItem}>Hello, {user?.name}</span>}
        {!!user || <a className="btn btn-outline-success my-2 my-sm-0" href="/oauth2/authorization/google">Login With Google</a>}
        {!!user && <a className="btn btn-outline-danger my-2 my-sm-0" href="/logout">Logout</a>}
      </div>
    </nav>
  )
}

export default Nav