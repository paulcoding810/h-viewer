# H Viewer

An Android application for extracting image content from websites.

## Features

- Web content extraction
- Image downloading and viewing
- Custom JavaScript parsing

## How It Works

H Viewer uses Rhino JavaScript engine to parse and extract content from web pages.

## Technical Stack

- **UI**: Jetpack Compose
- **Networking**: Ktor
- **Database**: Room
- **HTML Parsing**: JSoup
- **Image Loading**: Coil
- **JavaScript Engine**: Rhino

## Usage Guide

### Available Helper Functions

| Function | Description |
|----------|-------------|
| `fetch()` | Synchronous Ktor request to get Document (JSoup) |
| `xhr()` | Synchronous Ktor request to get JSON response |
| `atob()` | Base64 string decoder |

### Example Scripts

For sample scripts and usage examples, visit:
[H Viewer Scripts Repository](https://github.com/paulcoding810/h-viewer-scripts)

## References

- [VBook Extensions](https://github.com/Darkrai9x/vbook-extensions)

## License

[MIT](./LICENSE)

## Contributing
