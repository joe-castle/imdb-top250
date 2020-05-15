const watchedCount = document.getElementById('watchedCount')
const movies = Array.from(document.getElementsByClassName('movie'))

function getWatchedCount() {
  return document.getElementsByClassName('watched').length
}

function updateMoviesFromFiltersAndSearch() {
  const activeFilters = Object.keys(filters)
    .filter(filter => filters[filter])

  movies.forEach(movie => {
    movie.classList.remove('hide')
  })

  if (searchTerm.length > 0) {
      const search = new RegExp(searchTerm, 'gi')

      movies.filter(movie => !search.test(movie.id))
        .forEach(movie => movie.classList.add('hide'))
  }

  if (activeFilters.length > 0) {
      movies.filter(movie => !activeFilters.every(filter =>
        movie.children[1].children[1].children[0].children[3].children[4].textContent.includes(filter))
      )
      .forEach(movie => movie.classList.add('hide'))
  }
}

movies.forEach(el => {
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

const filters = {}

Array.from(document.getElementsByClassName('filter')).forEach(el => {
  const filter = el.textContent
  filters[filter] = false

  el.addEventListener('click', event => {
    el.classList.toggle('active')
    filters[filter] = !filters[filter]
    updateMoviesFromFiltersAndSearch()
  })
})

let searchTerm = ''

document.getElementById('search').addEventListener('keyup', event => {
    searchTerm = event.target.value;
    updateMoviesFromFiltersAndSearch()
})