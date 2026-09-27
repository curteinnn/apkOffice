export default function BottomNavbar() {
  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 border-t border-gray-200 bg-white/95 backdrop-blur">
      <div className="mx-auto flex max-w-md items-center justify-around px-4 py-3">
        <button className="flex flex-col items-center gap-1 text-black">
          <span className="text-xl">⌂</span>
          <span className="text-xs font-medium">Home</span>
        </button>

        <button className="flex flex-col items-center gap-1 text-gray-400 transition hover:text-black">
          <span className="text-xl">📅</span>
          <span className="text-xs font-medium">Absensi</span>
        </button>

        <button className="flex flex-col items-center gap-1 text-gray-400 transition hover:text-black">
          <span className="text-xl">📋</span>
          <span className="text-xs font-medium">Tugas</span>
        </button>

        <button className="flex flex-col items-center gap-1 text-gray-400 transition hover:text-black">
          <span className="text-xl">👤</span>
          <span className="text-xs font-medium">Profil</span>
        </button>
      </div>
    </nav>
  );
}
