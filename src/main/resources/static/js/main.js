const watchedCount = document.getElementById('watchedCount')

Array.from(document.getElementsByClassName('movie')).forEach(el => {
  el.addEventListener('click', event => {
    const movie = el.id

    console.log(`Fetching movie with: ${movie}`)

    fetch(`/movies/${movie}`, { method: 'POST'})
      .then(res => res.json())
      .then(res => {
        console.log(`Res: ${res}`)
        if (res) {
          el.classList.toggle('watched')
          watchedCount.textContent = getWatchedCount()
        }
      })
      .catch(err => console.error(err))
  })
})

function getWatchedCount () {
    return document.getElementsByClassName('watched').length
}