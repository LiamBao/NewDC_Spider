package com.cic.datacrawl.core.browser;

import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.AuthenticationEvent;
import org.eclipse.swt.browser.AuthenticationListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.browser.tools.ThreadDisplaySWT;
import com.cic.datacrawl.core.config.SpringConfiguration;

public abstract class AbstractJavaSWTWebBrowser extends AbstractJavaWebBrowser {
	private static final Logger LOG = Logger.getLogger(AbstractJavaSWTWebBrowser.class);
	protected Browser browser;

	protected abstract void registerHttpMonitor();

	private void create(Composite parent, int style) {
		browser = new Browser(parent, style);

		browser.setLayoutData(BorderLayout.CENTER);

		registerHttpMonitor();
		browser.addOpenWindowListener(getOpenWindowListener());
		browser.addProgressListener(getProgressListener());
		browser.addLocationListener(getLocationListener());

		browser.addAuthenticationListener(new AuthenticationListener() {
			@Override
			public void authenticate(AuthenticationEvent event) {
				event.doit = false;
			}
		});

		configBrowser();
		setAlertDialogs(false);

	}

	protected abstract void configBrowser();

	protected abstract void setAlertDialogs(boolean enabled);

	protected abstract OpenWindowListener getOpenWindowListener();

	protected abstract ProgressListener getProgressListener();

	protected abstract LocationListener getLocationListener();

	private Display display;

	protected Display getDisplay() {
		if (display == null)
			display = ThreadDisplaySWT.getInstance().getDisplay();

		return display;
	}

	@Override
	public void asyncExec(final Runnable runnable) {
		final RuntimeException[] runtimeException = new RuntimeException[1];
		final Error[] error = new Error[1];
		new Thread(new Runnable() {

			@Override
			public void run() {
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						try {
							runnable.run();
						} catch (RuntimeException e) {
							runtimeException[0] = e;
						} catch (Error e) {
							error[0] = e;
						}
					}
				});
			}
		}).start();
		if (error[0] != null)
			throw error[0];
		if (runtimeException[0] != null)
			throw runtimeException[0];
	}

	@Override
	public void syncExec(final Runnable runnable) {
		final RuntimeException[] runtimeException = new RuntimeException[1];
		final Error[] error = new Error[1];

		getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch (RuntimeException e) {
					runtimeException[0] = e;
				} catch (Error e) {
					error[0] = e;
				}
			}
		});

		if (error[0] != null)
			throw error[0];
		if (runtimeException[0] != null)
			throw runtimeException[0];
	}

	@Override
	public void show(JPanel jPanel) {
		Canvas canvas = null;
		if (jPanel != null) {
			canvas = new Canvas();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(canvas, BorderLayout.CENTER);
			canvas.addNotify();
		}
		Shell shell = null;
		if (canvas != null) {
			shell = SWT_AWT.new_Shell(display, canvas);
		} else {
			shell = new Shell(display);
		}
		ThreadDisplaySWT.getInstance().setShell(shell);
		shell.setLayout(new FillLayout());
		create(shell, getBrowserType());
		browser.setBounds(shell.getClientArea());
		if (canvas == null) {
			SpringConfiguration config = (SpringConfiguration) ApplicationContext.getInstance()
					.getBean("config");
			if (config.isShowBrowser()) {
				shell.setSize(800, 600);
				shell.open();
				// browser.setBounds(shell.getClientArea());
				shell.addShellListener(new ShellAdapter() {
					@Override
					public void shellClosed(ShellEvent e) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
								}
								System.exit(0);
							}
						}).start();
					}

				});
			}
		}
	}

	protected abstract int getBrowserType();
}
