export default function () {
  return (
    <main className="bg-linear-to-br flex justify-center items-center from-blue-400 to-white min-h-screen">
      <section className="m-10">
        <h1 className="title text-2xl px-5">WELCOME TO MYIN</h1>

        <div className="kalender grid gap-x-50 lg:grid-cols-2 mt-5">
          <div className="bg-blue-300 w-100 h-10"></div>
          <div className="bg-blue-300 w-100 h-10"></div>
        </div>
        <div className="card grid gap-x-50 gap-y-10 grid-cols-1 md:grid-cols-2 my-5 mt-10">
          <div className="h-40 w-100 bg-blue-200 rounded-lg p-5 text-center">
            <p className="text-blue-600 ">
              Rekam kehadiran anda saat <br /> memulai pekerjaan
            </p>
          </div>
          <div className="h-40 w-100 bg-blue-200 rounded-lg"></div>
          <div className="h-40 w-100 bg-blue-200 rounded-lg"></div>
          <div className="h-40 w-100 bg-blue-200 rounded-lg"></div>
        </div>
      </section>
    </main>
  );
}
