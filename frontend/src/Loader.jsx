import React from 'react'

import styles from './Loader.module.css'

function Loader({ classes }) {
  return (
    <div className={`${styles['lds-ring']} ${classes}`}>
      <div/>
      <div/>
      <div/>
      <div/>
    </div>
  )
}

export default Loader

